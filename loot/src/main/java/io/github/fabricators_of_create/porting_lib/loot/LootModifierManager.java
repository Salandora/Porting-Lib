package io.github.fabricators_of_create.porting_lib.loot;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.Deserializers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class LootModifierManager extends SimpleJsonResourceReloadListener implements IdentifiableResourceReloadListener {
	public static final Logger LOGGER = LogManager.getLogger();
	public static final Gson GSON_INSTANCE = Deserializers.createFunctionSerializer().create();

	private Map<ResourceLocation, IGlobalLootModifier> registeredLootModifiers = ImmutableMap.of();
	private static final String folder = "loot_modifiers";

	public LootModifierManager() {
		super(GSON_INSTANCE, folder);
	}

	@Override
	protected void apply(@NotNull Map<ResourceLocation, JsonElement> resourceList,
						 ResourceManager resourceManagerIn,
						 @NotNull ProfilerFiller profilerIn) {
		Builder<ResourceLocation, IGlobalLootModifier> builder = ImmutableMap.builder();
		List<ResourceLocation> finalLocations = new ArrayList<>();
		ResourceLocation resourcelocation = new ResourceLocation("forge","loot_modifiers/global_loot_modifiers.json");
		//read in all data files from forge:loot_modifiers/global_loot_modifiers in order to do layering
		for(Resource iresource : resourceManagerIn.getResourceStack(resourcelocation)) {
			try (   InputStream inputstream = iresource.open();
					Reader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));
			) {
				JsonObject jsonobject = GsonHelper.fromJson(GSON_INSTANCE, reader, JsonObject.class);
				boolean replace = jsonobject.get("replace").getAsBoolean();
				if (replace)
					finalLocations.clear();
				JsonArray entryList = jsonobject.get("entries").getAsJsonArray();
				for(JsonElement entry : entryList) {
					ResourceLocation loc = new ResourceLocation(entry.getAsString());
					finalLocations.remove(loc); //remove and re-add if needed, to update the ordering.
					finalLocations.add(loc);
				}
			}

			catch (RuntimeException | IOException ioexception) {
				LOGGER.error("Couldn't read global loot modifier list {} in data pack {}", resourcelocation, iresource.sourcePackId(), ioexception);
			}
		}
		//use layered config to fetch modifier data files (modifiers missing from config are disabled)
		for (ResourceLocation location : finalLocations)
		{
			JsonElement json = resourceList.get(location);
			IGlobalLootModifier.DIRECT_CODEC.parse(JsonOps.INSTANCE, json)
					// log error if parse fails
					.resultOrPartial(errorMsg -> LOGGER.warn("Could not decode GlobalLootModifier with json id {} - error: {}", location, errorMsg))
					// add loot modifier if parse succeeds
					.ifPresent(modifier -> builder.put(location, modifier));
		}
		this.registeredLootModifiers = builder.build();
	}

	/**
	 * An immutable collection of the registered loot modifiers in layered order.
	 */
	public Collection<IGlobalLootModifier> getAllLootMods() {
		return registeredLootModifiers.values();
	}

	public static final LootModifierManager INSTANCE = new LootModifierManager();

	public static LootModifierManager getLootModifierManager() {
		return INSTANCE;
	}

	@Override
	public ResourceLocation getFabricId() {
		return PortingLibLoot.id("loot_modifier_manager");
	}
}
