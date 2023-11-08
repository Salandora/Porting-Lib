package me.alphamode.forgetags;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public class TagHelper {
	public static <V> Optional<Holder.Reference<V>> getReverseTag(Registry<V> registry, @NotNull V value) {
		return registry.getHolder(registry.getResourceKey(value).get());
	}

	public static <V> Optional<V> getRandomElement(Registry<V> registry, TagKey<V> tag, RandomSource random) {
		return Util.getRandomSafe(getContents(registry, tag), random);
	}

	public static <V> List<V> getContents(Registry<V> registry, TagKey<V> tag) {
		return registry.getTag(tag).map(holders -> holders.stream().map(Holder::value).toList()).orElse(Collections.emptyList());
	}
}
