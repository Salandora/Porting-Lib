package io.github.fabricators_of_create.porting_lib.extensions.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public interface IShearable {
	default boolean isShearable(ItemStack item, Level world, BlockPos pos) {
		return false;
	}

	default List<ItemStack> onSheared(@Nullable Player player, ItemStack item, Level world, BlockPos pos, int fortune) {
		return Collections.emptyList();
	}
}
