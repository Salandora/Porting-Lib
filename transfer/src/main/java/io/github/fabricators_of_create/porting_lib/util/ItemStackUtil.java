package io.github.fabricators_of_create.porting_lib.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;

public class ItemStackUtil {
	public static boolean areTagsEqual(ItemStack stack1, ItemStack stack2) {
		CompoundTag tag1 = stack1.getTag();
		CompoundTag tag2 = stack2.getTag();
		if (tag1 == null) {
			return tag2 == null;
		} else {
			return tag1.equals(tag2);
		}
	}

	public static ItemStack[] createEmptyStackArray(int size) {
		ItemStack[] stacks = new ItemStack[size];
		Arrays.fill(stacks, ItemStack.EMPTY);
		return stacks;
	}
}
