package io.github.fabricators_of_create.porting_lib.extensions.mixin.common;

import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib.extensions.extensions.BlockExtensions;
import net.minecraft.world.level.block.Block;

@Mixin(Block.class)
public abstract class BlockMixin implements BlockExtensions {
}
