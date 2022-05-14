package io.github.fabricators_of_create.porting_lib.extensions;

import io.github.fabricators_of_create.porting_lib.block.ChunkUnloadListeningBlockEntity;
import io.github.fabricators_of_create.porting_lib.util.OnLoadBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockEntityExtensions extends OnLoadBlockEntity, ChunkUnloadListeningBlockEntity {
	default CompoundTag getExtraCustomData() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default void deserializeNBT(BlockState state, CompoundTag nbt) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default void invalidateCaps() {}
}
