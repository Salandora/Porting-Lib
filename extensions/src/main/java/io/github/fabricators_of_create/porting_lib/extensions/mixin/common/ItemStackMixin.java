package io.github.fabricators_of_create.porting_lib.extensions.mixin.common;

import io.github.fabricators_of_create.porting_lib.extensions.extensions.INBTSerializableCompound;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements INBTSerializableCompound {
	@Shadow
	public abstract CompoundTag save(CompoundTag compoundTag);

	@Shadow
	public abstract void setTag(@Nullable CompoundTag compoundTag);

	@Shadow
	public abstract Item getItem();

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag nbt = new CompoundTag();
		this.save(nbt);
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		this.setTag(ItemStack.of(nbt).getTag());
	}
}
