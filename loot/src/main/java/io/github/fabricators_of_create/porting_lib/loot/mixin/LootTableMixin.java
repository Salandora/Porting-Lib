package io.github.fabricators_of_create.porting_lib.loot.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.fabricators_of_create.porting_lib.loot.LootCollector;
import io.github.fabricators_of_create.porting_lib.loot.extensions.LootTableExtensions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Objects;
import java.util.function.Consumer;

@Mixin(LootTable.class)
public class LootTableMixin implements LootTableExtensions {
	@Unique
	private ResourceLocation lootTableId;

	@Override
	public void setLootTableId(final ResourceLocation id) {
		if (this.lootTableId != null)
			throw new IllegalStateException("Attempted to rename loot table from '" + this.lootTableId + "' to '" + id + "': this is not supported");
		this.lootTableId = Objects.requireNonNull(id);
	}

	@Override
	public ResourceLocation getLootTableId() {
		return this.lootTableId;
	}

	@ModifyVariable(
			method = "getRandomItemsRaw(Lnet/minecraft/world/level/storage/loot/LootContext;Ljava/util/function/Consumer;)V",
			at = @At("HEAD"),
			argsOnly = true
	)
	private Consumer<ItemStack> wrapConsumer(Consumer<ItemStack> output) {
		return new LootCollector(output);
	}

	@Inject(
			method = "getRandomItemsRaw(Lnet/minecraft/world/level/storage/loot/LootContext;Ljava/util/function/Consumer;)V",
			at = @At("RETURN")
	)
	private void finishCollectingLoot(LootContext context, Consumer<ItemStack> output, CallbackInfo ci) {
		if (output instanceof LootCollector collector)
			collector.finish(this.lootTableId, context);
	}
}
