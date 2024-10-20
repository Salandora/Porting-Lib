package io.github.fabricators_of_create.porting_lib.transfer.mixin.common;

import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib.extensions.transfer.LevelExtensions;
import io.github.fabricators_of_create.porting_lib.transfer.cache.EmptyFluidLookupCache;
import io.github.fabricators_of_create.porting_lib.transfer.cache.EmptyItemLookupCache;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

@Mixin(Level.class)
public abstract class LevelMixin implements LevelExtensions {
	@Override
	public BlockApiCache<Storage<ItemVariant>, Direction> port_lib$getItemCache(BlockPos pos) {
		// uh oh. Not a ClientLevel or ServerLevel!
		return new EmptyItemLookupCache(pos);
	}

	@Override
	public BlockApiCache<Storage<FluidVariant>, Direction> port_lib$getFluidApiCache(BlockPos pos) {
		// uh oh. Not a ClientLevel or ServerLevel!
		return new EmptyFluidLookupCache(pos);
	}
}
