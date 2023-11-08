package io.github.fabricators_of_create.porting_lib.extensions.mixin.common;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.fabricators_of_create.porting_lib.extensions.extensions.EntityExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.extensions.INBTSerializableCompound;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;

import java.util.Collection;
import org.jetbrains.annotations.Nullable;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityExtensions, INBTSerializableCompound {
	private static final String EXTRA_DATA_KEY = "ForgeData";

	@Shadow
	public Level level;
	@Shadow
	@Nullable
	protected abstract String getEncodeId();
	@Shadow
	public abstract CompoundTag saveWithoutId(CompoundTag compoundTag);
	@Shadow
	public abstract void load(CompoundTag compoundTag);

	@Unique
	private CompoundTag port_lib$extraCustomData;
	@Unique
	private Collection<ItemEntity> port_lib$captureDrops = null;

	// CAPTURE DROPS
	@WrapWithCondition(
			method = "spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"
			)
	)
	public boolean port_lib$captureDrops(Level level, Entity entity) {
		if (captureDrops() != null && entity instanceof ItemEntity item) {
			captureDrops().add(item);
			return false;
		}
		return true;
	}

	@Unique
	@Override
	public Collection<ItemEntity> captureDrops() {
		return port_lib$captureDrops;
	}

	@Unique
	@Override
	public Collection<ItemEntity> captureDrops(Collection<ItemEntity> value) {
		Collection<ItemEntity> ret = port_lib$captureDrops;
		port_lib$captureDrops = value;
		return ret;
	}
	// EXTRA CUSTOM DATA

	@Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"))
	public void port_lib$beforeWriteCustomData(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir) {
		if (port_lib$extraCustomData != null && !port_lib$extraCustomData.isEmpty()) {
			tag.put(EXTRA_DATA_KEY, port_lib$extraCustomData);
		}
	}

	@Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"))
	public void port_lib$beforeReadCustomData(CompoundTag tag, CallbackInfo ci) {
		if (tag.contains(EXTRA_DATA_KEY)) {
			port_lib$extraCustomData = tag.getCompound(EXTRA_DATA_KEY);
		}
	}

	@Unique
	@Override
	public CompoundTag getExtraCustomData() {
		if (port_lib$extraCustomData == null) {
			port_lib$extraCustomData = new CompoundTag();
		}
		return port_lib$extraCustomData;
	}

	@Unique
	@Override
	public CompoundTag serializeNBT() {
		CompoundTag ret = new CompoundTag();
		String id = getEncodeId();
		if (id != null) {
			ret.putString("id", id);
		}
		return saveWithoutId(ret);
	}

	@Unique
	@Override
	public void deserializeNBT(CompoundTag nbt) {
		load(nbt);
	}
}
