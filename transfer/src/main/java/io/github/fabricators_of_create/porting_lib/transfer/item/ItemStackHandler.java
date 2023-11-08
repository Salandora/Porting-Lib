package io.github.fabricators_of_create.porting_lib.transfer.item;

import it.unimi.dsi.fastutil.objects.ObjectAVLTreeSet;

import io.github.fabricators_of_create.porting_lib.extensions.extensions.INBTSerializable;
import io.github.fabricators_of_create.porting_lib.util.DualSortedSetIterator;
import io.github.fabricators_of_create.porting_lib.util.EmptySortedSet;
import io.github.fabricators_of_create.porting_lib.util.ItemStackUtil;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

public class ItemStackHandler implements SlottedStackStorage, INBTSerializable<CompoundTag>  {
	private final List<ItemStackHandlerSlot> slots;
	private final SortedSet<ItemStackHandlerSlot> nonEmptySlots;
	private final Map<Item, SortedSet<ItemStackHandlerSlot>> lookup;

	public ItemStackHandler() {
		this(1);
	}

	public ItemStackHandler(int stacks) {
		this(ItemStackUtil.createEmptyStackArray(stacks));
	}

	public ItemStackHandler(ItemStack[] stacks) {
		this.slots = new ArrayList<>(stacks.length);
		this.nonEmptySlots = createSlotSet();
		this.lookup = new HashMap<>();
		for (int i = 0; i < stacks.length; i++) {
			ItemStack stack = stacks[i];
			this.slots.add(makeSlot(i, stack));
		}
	}

	@Override
	public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		StoragePreconditions.notBlankNotNegative(resource, maxAmount);
		long inserted = 0;
		Iterator<ItemStackHandlerSlot> iter = getInsertableSlotsFor(resource);
		while (iter.hasNext()) {
			ItemStackHandlerSlot slot = iter.next();
			inserted += slot.insert(resource, maxAmount - inserted, transaction);
			if (inserted >= maxAmount) {
				break;
			}
		}
		return inserted;
	}

	@Override
	public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		StoragePreconditions.notBlankNotNegative(resource, maxAmount);
		Item item = resource.getItem();
		SortedSet<ItemStackHandlerSlot> slots = getSlotsContaining(item);
		if (slots.isEmpty())
			return 0;

		long extracted = 0;
		for (ItemStackHandlerSlot slot : slots) {
			extracted += slot.extract(resource, maxAmount - extracted, transaction);
			if (extracted >= maxAmount) {
				break;
			}
		}
		return extracted;
	}

	@Override
	@Nullable
	public StorageView<ItemVariant> exactView(ItemVariant resource) {
		StoragePreconditions.notBlank(resource);
		SortedSet<ItemStackHandlerSlot> slots = getSlotsContaining(resource.getItem());
		return slots.isEmpty() ? null : slots.first();
	}

	@Override
	public Iterable<StorageView<ItemVariant>> nonEmptyViews() {
		return (Iterable) this.nonEmptySlots;
	}

	@Override
	public Iterator<StorageView<ItemVariant>> nonEmptyIterator() {
		return (Iterator) this.nonEmptySlots.iterator();
	}

	@Override
	public int getSlotCount() {
		return slots.size();
	}

	@Override
	public ItemStackHandlerSlot getSlot(int slot) {
		return this.slots.get(slot);
	}

	@Override
	public List<SingleSlotStorage<ItemVariant>> getSlots() {
		//noinspection unchecked
		return (List) slots;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return getSlot(slot).getStack();
	}

	@Override
	public void setStackInSlot(int slot, @NotNull ItemStack stack) {
		getSlot(slot).setNewStack(stack);
	}

	public ItemVariant getVariantInSlot(int slot) {
		return getSlot(slot).getResource();
	}

	public int getSlotLimit(int slot) {
		return getStackInSlot(slot).getMaxStackSize();
	}

	protected int getStackLimit(int slot, ItemVariant resource) {
		return Math.min(getSlotLimit(slot), resource.getItem().getMaxStackSize());
	}

	public SortedSet<ItemStackHandlerSlot> getSlotsContaining(Item item) {
		return lookup.containsKey(item) ? lookup.get(item) : EmptySortedSet.cast();
	}

	protected void onLoad() {
	}

	protected void onContentsChanged(int slot) {
	}

	public boolean empty() {
		return nonEmptySlots.isEmpty();
	}

	public void setSize(int size) {
		this.slots.clear();
		nonEmptySlots.clear();
		lookup.clear();
		for (int i = 0; i < size; i++) {
			this.slots.add(makeSlot(i, ItemStack.EMPTY));
		}
	}

	protected ItemStackHandlerSlot makeSlot(int index, ItemStack stack) {
		return new ItemStackHandlerSlot(index, this, stack);
	}

	@Override
	public CompoundTag serializeNBT() {
		ListTag nbtTagList = new ListTag();
		for (ItemStackHandlerSlot slot : this.slots) {
			ItemStack stack = slot.getStack();
			if (!stack.isEmpty()) {
				CompoundTag itemTag = new CompoundTag();
				itemTag.putInt("Slot", slot.getIndex());
				stack.save(itemTag);
				nbtTagList.add(itemTag);
			}
		}
		CompoundTag nbt = new CompoundTag();
		nbt.put("Items", nbtTagList);
		nbt.putInt("Size", this.slots.size());
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		setSize(nbt.contains("Size", Tag.TAG_INT) ? nbt.getInt("Size") : slots.size());
		ListTag tagList = nbt.getList("Items", Tag.TAG_COMPOUND);
		for (int i = 0; i < tagList.size(); i++) {
			CompoundTag itemTags = tagList.getCompound(i);
			int slot = itemTags.getInt("Slot");

			if (slot >= 0 && slot < slots.size()) {
				ItemStack stack = ItemStack.of(itemTags);
				this.slots.get(slot).setNewStackInternal(stack);
			}
		}
		onLoad();
	}

	void onStackChange(ItemStackHandlerSlot slot, ItemStack oldStack, ItemStack newStack) {
		if (ItemStack.isSame(oldStack, newStack)) {
			return;
		}
		SortedSet<ItemStackHandlerSlot> oldItemSlots = this.getSlotsContaining(oldStack.getItem());
		if (!oldItemSlots.isEmpty()) {
			oldItemSlots.remove(slot);
		}
		lookup.computeIfAbsent(newStack.getItem(), $ -> createSlotSet()).add(slot);
		if (oldStack.isEmpty()) {
			nonEmptySlots.add(slot);
		} else if (newStack.isEmpty()) {
			nonEmptySlots.remove(slot);
		}
	}

	void initSlot(ItemStackHandlerSlot slot) {
		ItemStack stack = slot.getStack();
		lookup.computeIfAbsent(stack.getItem(), $ -> createSlotSet()).add(slot);
		if (!stack.isEmpty()) {
			nonEmptySlots.add(slot);
		}
	}

	@Override
	public String toString() {
		return  getClass().getSimpleName() + '[' + this.slots + ']';
	}

	private Iterator<ItemStackHandlerSlot> getInsertableSlotsFor(ItemVariant variant) {
		SortedSet<ItemStackHandlerSlot> slots = this.getSlotsContaining(variant.getItem());
		SortedSet<ItemStackHandlerSlot> emptySlots = this.getSlotsContaining(Items.AIR);
		if (slots.isEmpty()) {
			return emptySlots.isEmpty() ? Collections.emptyIterator() : emptySlots.iterator();
		} else {
			return emptySlots.isEmpty() ? slots.iterator() : new DualSortedSetIterator<>(slots, emptySlots);
		}
	}

	private static SortedSet<ItemStackHandlerSlot> createSlotSet() {
		return new ObjectAVLTreeSet<>(Comparator.comparingInt(ItemStackHandlerSlot::getIndex));
	}
}
