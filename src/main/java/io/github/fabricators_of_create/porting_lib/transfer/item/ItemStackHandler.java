package io.github.fabricators_of_create.porting_lib.transfer.item;

import com.google.common.collect.Multimap;

import com.google.common.collect.TreeMultimap;

import io.github.fabricators_of_create.porting_lib.transfer.ExtendedStorage;
import io.github.fabricators_of_create.porting_lib.util.INBTSerializable;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Predicate;

public class ItemStackHandler implements ExtendedStorage<ItemVariant>, INBTSerializable<CompoundTag> {
	private final List<ItemStackHandlerSlot> slots;
	// internal, package-private
	final Multimap<Item, ItemStackHandlerSlot> lookup;
	final SortedSet<ItemStackHandlerSlot> nonEmptySlots;

	public ItemStackHandler() {
		this(1);
	}

	public ItemStackHandler(ItemStack[] stacks) {
		this(stacks.length);
		for (int i = 0; i < stacks.length; i++) {
			setStackInSlot(i, stacks[i]);
		}
	}

	public ItemStackHandler(int size) {
		this.slots = new ArrayList<>(size);
		this.lookup = TreeMultimap.create(Comparator.comparingInt(Item::hashCode), ItemStackHandlerSlot.COMPARATOR);
		this.nonEmptySlots = new TreeSet<>(ItemStackHandlerSlot.COMPARATOR);

		for (int i = 0; i < size; i++) {
			slots.add(createNewSlot(i));
		}
		// storages manage lookup, now all mapped to air
	}

	// API

	/**
	 * Called when the item within a slot changes. This is called during transactions to modify the state of the storage.
	 * This will often be called multiple times each for multiple different slots.
	 */
	protected void slotChangedItems(ItemStackHandlerSlot slot, Item oldItem, Item newItem) {
		lookup.get(oldItem).remove(slot);
		lookup.get(newItem).add(slot);

		if (oldItem == Items.AIR) { // no longer empty
			nonEmptySlots.add(slot);
		} else if (newItem == Items.AIR) { // changed to empty
			nonEmptySlots.remove(slot);
		}
	}

	/**
	 * Create a new slot to be part of this storage.
	 */
	protected ItemStackHandlerSlot createNewSlot(int index) {
		return new ItemStackHandlerSlot(index, this);
	}

	/**
	 * @return the total number of slots in this storage.
	 */
	public int getSlots() {
		return slots.size();
	}

	/**
	 * @return the slot at the given index
	 */
	public ItemStackHandlerSlot getSlot(int index) {
		return slots.get(index);
	}

	/**
	 * Immediately set the stack in the given slot.
	 */
	public void setStackInSlot(int index, ItemStack stack) {
		ItemStackHandlerSlot slot = getSlot(index);
		slot.setStack(stack);
		slot.markChanged();
	}

	/**
	 * The ItemStack returned by this method should never be modified.
	 * @return the current stack of the given slot
	 */
	public ItemStack getStackInSlot(int slot) {
		return getSlot(slot).stack;
	}

	/**
	 * @return the current item variant of the given slot
	 */
	public ItemVariant getVariantInSlot(int slot) {
		return getSlot(slot).variant;
	}

	/**
	 * @return the max stack size of the given slot as it is
	 */
	public int getSlotLimit(int slot) {
		return getStackInSlot(slot).getMaxStackSize();
	}

	/**
	 * @return the max stack size of the given slot as if it contained the given ItemVariant
	 */
	protected int getStackLimit(int slot, ItemVariant resource) {
		return Math.min(getSlotLimit(slot), resource.getItem().getMaxStackSize());
	}

	/**
	 * @return true if the given slot can hold the given ItemVariant
	 */
	public boolean isItemValid(int slot, ItemVariant resource) {
		return true;
	}

	/**
	 * Called when this storage loads from NBT data.
	 */
	protected void onLoad() {
	}

	/**
	 * Called when a slot's content is changed after a transaction is completed.
	 * This is often called multiple times, once for each modified slot.
	 */
	protected void onContentsChanged(int slot) {
	}

	/**
	 * Update the size of this storage, clearing all existing content.
	 */
	public void setSize(int size) {
		// add slots if new size is larger
		while (slots.size() < size)
			slots.add(createNewSlot(slots.size()));
		// remove slots if new size is smaller
		slots.removeIf(slot -> slot.index >= size);
		// clear data, also resets lookup and non-empty list
		slots.forEach(slot -> slot.setStack(ItemStack.EMPTY));
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + '{' + "size=" + slots.size() + ", slots=" + slots + '}';
	}

	// storage impl

	@Override
	public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		StoragePreconditions.notBlankNotNegative(resource, maxAmount);
		InsertableSlotIterator iterator = new InsertableSlotIterator(this, resource.getItem());
		long inserted = 0;
		while (inserted < maxAmount && iterator.hasNext()) {
			ItemStackHandlerSlot slot = iterator.next();
			if (!isItemValid(slot.index, resource))
				continue;
			long max = Math.min(maxAmount - inserted, getStackLimit(slot.index, resource));
			inserted += slot.insert(resource, max, transaction);
		}
		return inserted;
	}

	@Override
	public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		StoragePreconditions.notBlankNotNegative(resource, maxAmount);
		Iterator<ItemStackHandlerSlot> iterator = List.copyOf(lookup.get(resource.getItem())).iterator();
		long extracted = 0;
		while (extracted < maxAmount && iterator.hasNext()) {
			ItemStackHandlerSlot slot = iterator.next();
			extracted += slot.extract(resource, maxAmount - extracted, transaction);
		}
		return extracted;
	}

	@Nullable
	@Override
	public ResourceAmount<ItemVariant> extractMatching(Predicate<ItemVariant> predicate, long maxAmount, TransactionContext transaction) {
		ItemVariant variant = null;
		for (ItemStackHandlerSlot slot : nonEmptySlots) {
			ItemVariant resource = slot.getResource();
			if (predicate.test(resource)) {
				variant = resource;
				break;
			}
		}
		if (variant == null)
			return null;
		long extracted = extract(variant, maxAmount, transaction);
		return new ResourceAmount<>(variant, extracted);
	}

	@Override
	public Iterator<StorageView<ItemVariant>> iterator(TransactionContext transaction) {
		//noinspection unchecked, rawtypes
		return (Iterator) slots.iterator();
	}

	@Override
	public Iterator<? extends StorageView<ItemVariant>> nonEmptyViews() {
		return List.copyOf(nonEmptySlots).iterator();
	}

	// NBT

	@Override
	public CompoundTag serializeNBT() {
		ListTag itemList = new ListTag();
		// empty slots are skipped
		for (ItemStackHandlerSlot slot : nonEmptySlots) {
			CompoundTag itemData = new CompoundTag();
			itemData.putInt("Slot", slot.index);
			slot.stack.save(itemData);
			itemList.add(itemData);
		}
		CompoundTag nbt = new CompoundTag();
		nbt.put("Items", itemList);
		nbt.putInt("Size", slots.size());
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		// always set size to clear existing content
		setSize(nbt.contains("Size", Tag.TAG_INT) ? nbt.getInt("Size") : slots.size());
		ListTag itemList = nbt.getList("Items", Tag.TAG_COMPOUND);
		for (int i = 0; i < itemList.size(); i++) {
			CompoundTag itemData = itemList.getCompound(i);
			int slot = itemData.getInt("Slot");
			if (slot < 0 || slot >= slots.size())
				continue; // invalid data, don't crash from it

			ItemStack stack = ItemStack.of(itemData); // EMPTY if deserialization fails
			if (!stack.isEmpty())
				setStackInSlot(slot, stack);
		}

		onLoad();
	}
}
