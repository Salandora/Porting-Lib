package io.github.fabricators_of_create.porting_lib.loot.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import io.github.fabricators_of_create.porting_lib.loot.LootTableIdCondition;
import io.github.fabricators_of_create.porting_lib.loot.PortingLibLoot;
import io.github.fabricators_of_create.porting_lib.loot.extensions.LootContextExtensions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import org.jetbrains.annotations.Nullable;

@Mixin(LootContext.class)
public abstract class LootContextMixin implements LootContextExtensions {
	@Shadow
	@Nullable
	public abstract <T> T getParamOrNull(LootContextParam<T> lootContextParam);

	@Unique
	private ResourceLocation queriedLootTableId;

	@Override
	public void setQueriedLootTableId(ResourceLocation queriedLootTableId) {
		if (this.queriedLootTableId == null && queriedLootTableId != null)
			this.queriedLootTableId = queriedLootTableId;
	}

	@Override
	public ResourceLocation getQueriedLootTableId() {
		return this.queriedLootTableId == null ? LootTableIdCondition.UNKNOWN_LOOT_TABLE : this.queriedLootTableId;
	}

	@Override
	public int getLootingModifier() {
		return PortingLibLoot.getLootingLevel(getParamOrNull(LootContextParams.THIS_ENTITY), getParamOrNull(LootContextParams.KILLER_ENTITY), getParamOrNull(LootContextParams.DAMAGE_SOURCE));
	}
}
