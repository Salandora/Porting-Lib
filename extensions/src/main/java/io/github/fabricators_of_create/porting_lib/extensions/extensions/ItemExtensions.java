package io.github.fabricators_of_create.porting_lib.extensions.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.TippedArrowItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import org.jetbrains.annotations.Nullable;

public interface ItemExtensions {
	/**
	 * Determines if this Item has a special entity for when they are in the world.
	 * Is called when a EntityItem is spawned in the world, if true and
	 * Item#createCustomEntity returns non null, the EntityItem will be destroyed
	 * and the new Entity will be added to the world.
	 *
	 * @param stack The current item stack
	 * @return True of the item has a custom entity, If true,
	 *         Item#createCustomEntity will be called
	 */
	default boolean hasCustomEntity(ItemStack stack) {
		return false;
	}

	/**
	 * This function should return a new entity to replace the dropped item.
	 * Returning null here will not kill the EntityItem and will leave it to
	 * function normally. Called when the item it placed in a level.
	 *
	 * @param level     The level object
	 * @param location  The EntityItem object, useful for getting the position of
	 *                  the entity
	 * @param stack The current item stack
	 * @return A new Entity object to spawn or null
	 */
	@Nullable
	default Entity createEntity(Level level, Entity location, ItemStack stack) {
		return null;
	}
}
