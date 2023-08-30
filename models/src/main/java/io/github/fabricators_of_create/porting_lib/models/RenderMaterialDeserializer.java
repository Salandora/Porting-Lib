package io.github.fabricators_of_create.porting_lib.models;

import com.google.gson.*;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Type;
import java.util.function.BiConsumer;

public class RenderMaterialDeserializer implements JsonDeserializer<RenderMaterial> {
	@Override
	public RenderMaterial deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		MaterialFinder finder = RendererAccess.INSTANCE.getRenderer().materialFinder();
		JsonObject obj = json.getAsJsonObject();
		forEachSpriteIndex(obj, "blendMode", (spriteIndex, jsonElement) -> finder.blendMode(BlendMode.fromRenderLayer(RenderTypeUtil.get(new ResourceLocation(jsonElement.getAsString())))));
		forEachSpriteIndex(obj, "disableColorIndex", (spriteIndex, jsonElement) -> finder.disableColorIndex(jsonElement.getAsBoolean()));
		forEachSpriteIndex(obj, "disableDiffuse", (spriteIndex, jsonElement) -> finder.disableDiffuse(jsonElement.getAsBoolean()));
		forEachSpriteIndex(obj, "disableAo", (spriteIndex, jsonElement) -> finder.ambientOcclusion(jsonElement.getAsBoolean() ? TriState.FALSE : TriState.DEFAULT));
		forEachSpriteIndex(obj, "emissive", (spriteIndex, jsonElement) -> finder.emissive(jsonElement.getAsBoolean()));
		return finder.find();
	}

	public void forEachSpriteIndex(JsonObject obj, String key, BiConsumer<Integer, JsonElement> matFunc) {
		if (obj.has(key)) {
			JsonArray array = obj.getAsJsonArray(key);
			for (int i = 0; i < array.size(); i++) {
				matFunc.accept(i, array.get(i));
			}
		}
	}
}
