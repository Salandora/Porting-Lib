package io.github.fabricators_of_create.porting_lib.core.util;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.Tag;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.UnknownNullability;

/**
 * Used to declare that the implementation is implemented via mixin
 * @param <T>
 */
@ApiStatus.Internal
public interface DefaultNbtSerializable<T extends Tag> extends INBTSerializable<T> {
	@Override
	@UnknownNullability
	default T serializeNBT(HolderLookup.Provider provider) {
		throw PortingLib.createMixinException("NBT serialization");
	}

	@Override
	default void deserializeNBT(HolderLookup.Provider provider, T nbt) {
		throw PortingLib.createMixinException("NBT serialization");
	}
}
