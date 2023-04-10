package io.github.fabricators_of_create.porting_lib.transfer.item;

import com.google.common.collect.AbstractIterator;

import net.minecraft.world.item.Item;

import net.minecraft.world.item.Items;

import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

/**
 * An iterator for insertable slots of an {@link ItemStackHandler}. Given a handler and item,
 * iterates through all slots which either contain the item or are empty, in ascending index order.
 */
public class InsertableSlotIterator extends AbstractIterator<ItemStackHandlerSlot> {
	private final Iterator<ItemStackHandlerSlot> slots;
	private final Iterator<ItemStackHandlerSlot> emptySlots;

	private ItemStackHandlerSlot nextSlot;
	private ItemStackHandlerSlot nextEmptySlot;

	public InsertableSlotIterator(ItemStackHandler handler, Item item) {
		this.slots = handler.lookup.get(item).iterator();
		this.emptySlots = handler.lookup.get(Items.AIR).iterator();
	}

	@Nullable
	@Override
	protected ItemStackHandlerSlot computeNext() {
		// refresh stored slots
		if (nextSlot == null && slots.hasNext())
			nextSlot = slots.next();
		if (nextEmptySlot == null && emptySlots.hasNext())
			nextEmptySlot = emptySlots.next();

		if (nextSlot == null) { // out of filled slots
			if (nextEmptySlot == null) // out of empty too
				return endOfData();
			return consumeSlot(nextEmptySlot);
		} else if (nextEmptySlot == null) { // only out of empty
			return consumeSlot(nextSlot);
		}
		// neither null, return lower index
		if (nextSlot.index < nextEmptySlot.index) {
			return consumeSlot(nextSlot);
		} else {
			return consumeSlot(nextEmptySlot);
		}
	}

	private ItemStackHandlerSlot consumeSlot(ItemStackHandlerSlot slot) {
		if (slot == nextSlot)
			nextSlot = null;
		else if (slot == nextEmptySlot)
			nextEmptySlot = null;
		return slot;
	}
}
