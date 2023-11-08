package io.github.fabricators_of_create.porting_lib.tool.mixin;

import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib.tool.extensions.BlockStateExtensions;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BlockState.class)
public abstract class BlockStateMixin implements BlockStateExtensions {
}
