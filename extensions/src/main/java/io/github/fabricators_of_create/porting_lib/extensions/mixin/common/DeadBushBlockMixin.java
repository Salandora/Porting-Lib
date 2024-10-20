package io.github.fabricators_of_create.porting_lib.extensions.mixin.common;

import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib.extensions.extensions.IShearable;
import net.minecraft.world.level.block.DeadBushBlock;

@Mixin(DeadBushBlock.class)
public abstract class DeadBushBlockMixin implements IShearable {
}
