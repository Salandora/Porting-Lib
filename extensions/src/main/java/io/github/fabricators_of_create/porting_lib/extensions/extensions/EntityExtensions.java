package io.github.fabricators_of_create.porting_lib.extensions.extensions;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.Collection;

public interface EntityExtensions {
	default CompoundTag getExtraCustomData() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default Collection<ItemEntity> captureDrops() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default Collection<ItemEntity> captureDrops(Collection<ItemEntity> value) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
