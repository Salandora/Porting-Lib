package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.extensions.IShearable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.level.block.TallGrassBlock;

@Mixin(TallGrassBlock.class)
public abstract class TallGrassBlockMixin implements IShearable {
}
