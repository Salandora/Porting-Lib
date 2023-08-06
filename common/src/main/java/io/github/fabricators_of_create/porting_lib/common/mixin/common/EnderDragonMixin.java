package io.github.fabricators_of_create.porting_lib.common.mixin.common;

import io.github.fabricators_of_create.porting_lib.block.EntityDestroyBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EnderDragon.class)
public class EnderDragonMixin extends Mob {
    @Unique
    private boolean customLogic = false;
    @Unique
    private boolean shouldBreak = false;

    protected EnderDragonMixin(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    @Redirect(method = "checkWalls", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/tags/TagKey;)Z", ordinal = 1))
    public boolean port_lib$blockStateIsImmuneCheck(BlockState instance, TagKey<Block> tagKey) {
        if (customLogic)
            return !shouldBreak; // Need to invert here cause the original call will invert it too

        return instance.is(tagKey);
    }

    @Inject(method = "checkWalls", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    public void port_lib$checkWalls(AABB area, CallbackInfoReturnable<Boolean> cir, int i, int j, int k, int l, int m, int n, boolean bl, boolean bl2, int o, int p, int q, BlockPos blockPos) {
        BlockState blockState = this.level.getBlockState(blockPos);
        if (blockState.getBlock() instanceof EntityDestroyBlock destroyBlock) {
            customLogic = true;
            shouldBreak = destroyBlock.canEntityDestroy(blockState, this.level, blockPos, this);
        } else
            customLogic = false;
    }
}
