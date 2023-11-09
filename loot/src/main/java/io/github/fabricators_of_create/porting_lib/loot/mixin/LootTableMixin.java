package io.github.fabricators_of_create.porting_lib.loot.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import io.github.fabricators_of_create.porting_lib.extensions.extensions.LootTableExtensions;
import io.github.fabricators_of_create.porting_lib.util.PortingHooks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;

@Mixin(LootTable.class)
public class LootTableMixin implements LootTableExtensions {
	private ResourceLocation lootTableId;

	@Override
	public void setLootTableId(final ResourceLocation id) {
		if (this.lootTableId != null) throw new IllegalStateException("Attempted to rename loot table from '" + this.lootTableId + "' to '" + id + "': this is not supported");
		this.lootTableId = java.util.Objects.requireNonNull(id);
	}

	@Override
	public ResourceLocation getLootTableId() { return this.lootTableId; }

	@ModifyReturnValue(method = "getRandomItems(Lnet/minecraft/world/level/storage/loot/LootContext;)Lit/unimi/dsi/fastutil/objects/ObjectArrayList;", at = @At("RETURN"))
	public ObjectArrayList<ItemStack> port_lib$modifyGlobalLootTable(ObjectArrayList<ItemStack> list, LootContext context) {
		return PortingHooks.modifyLoot(getLootTableId(), list, context);
	}
}
