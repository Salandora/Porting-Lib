package io.github.fabricators_of_create.porting_lib.extensions.mixin.common;

import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib.extensions.extensions.IShearable;
import net.minecraft.world.level.block.TallGrassBlock;

@Mixin(TallGrassBlock.class)
public abstract class TallGrassBlockMixin implements IShearable {
}
