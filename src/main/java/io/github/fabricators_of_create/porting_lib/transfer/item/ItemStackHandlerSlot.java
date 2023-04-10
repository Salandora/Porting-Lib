package io.github.fabricators_of_create.porting_lib.transfer.item;

import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandlerSlot.Snapshot;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Comparator;

/**
 * A single slot of an {@link ItemStackHandler}. Can act as a storage on its own.
 */
public class ItemStackHandlerSlot extends SnapshotParticipant<Snapshot> implements SingleSlotStorage<ItemVariant> {
	public static final Comparator<ItemStackHandlerSlot> COMPARATOR = Comparator.comparingInt(slot -> slot.index);

	public final int index;
	public final ItemStackHandler handler;

	protected ItemStack stack;
	protected ItemVariant variant;

	public ItemStackHandlerSlot(int index, ItemStackHandler handler) {
		this.index = index;
		this.handler = handler;
		setStack(ItemStack.EMPTY);
	}

	protected void setStack(ItemStack stack) {
		setContent(stack, ItemVariant.of(stack));
	}

	protected void setContent(ItemStack stack, ItemVariant variant) {
		// null check: handle first call on creation, avoid NPE
		Item oldItem = this.stack == null ? Items.BARRIER : this.stack.getItem();
		Item newItem = stack.getItem();
		this.stack = stack;
		this.variant = variant;

		if (oldItem != newItem)
			handler.slotChangedItems(this, oldItem, newItem);
	}

	@Override
	protected void onFinalCommit() {
		markChanged();
	}

	/**
	 * Notify this slot's handler that this slot has been changed.
	 */
	public void markChanged() {
		handler.onContentsChanged(this);
	}

	/**
	 * @return the stack currently stored in this slot
	 */
	public ItemStack getStack() {
		return stack;
	}

	@Override
	public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		StoragePreconditions.notBlankNotNegative(resource, maxAmount);
		if (isResourceBlank()) {
			int toInsert = (int) Math.min(resource.getItem().getMaxStackSize(), maxAmount);
			ItemStack stack = resource.toStack(toInsert);
			updateSnapshots(transaction);
			setStack(stack);
			return toInsert;
		} else if (resource.matches(stack)) {
			int space = stack.getMaxStackSize() - stack.getCount();
			int toInsert = (int) Math.min(space, maxAmount);
			ItemStack stack = ItemHandlerHelper.growCopy(this.stack, toInsert);
			updateSnapshots(transaction);
			setStack(stack);
			return toInsert;
		}
		return 0;
	}

	@Override
	public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		StoragePreconditions.notBlankNotNegative(resource, maxAmount);
		if (!resource.matches(stack))
			return 0;

		long amount = getAmount();
		long toExtract = Math.min(maxAmount, amount);
		int newAmount = (int) (amount - toExtract);
		boolean emptied = newAmount <= 0;

		ItemStack newStack = emptied ? ItemStack.EMPTY : ItemHandlerHelper.copyStackWithSize(stack, newAmount);
		updateSnapshots(transaction);

		// avoid ItemVariant.of
		setContent(newStack, variant);

		return toExtract;
	}

	@Override
	public boolean isResourceBlank() {
		return variant.isBlank();
	}

	@Override
	public ItemVariant getResource() {
		return variant;
	}

	@Override
	public long getAmount() {
		return stack.getCount();
	}

	@Override
	public long getCapacity() {
		return stack.getMaxStackSize();
	}

	@Override
	protected Snapshot createSnapshot() {
		return new Snapshot(stack, variant);
	}

	@Override
	protected void readSnapshot(Snapshot snapshot) {
		// variant is reused to avoid potentially copying a lot of NBT often
		setContent(snapshot.stack, snapshot.variant);
	}

	protected record Snapshot(ItemStack stack, ItemVariant variant) {
	}
}
