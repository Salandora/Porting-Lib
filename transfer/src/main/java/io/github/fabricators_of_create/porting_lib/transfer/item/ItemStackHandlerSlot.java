package io.github.fabricators_of_create.porting_lib.transfer.item;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class ItemStackHandlerSlot extends SingleStackStorage {
	private final int index;
	private final ItemStackHandler handler;
	private ItemStack stack = ItemStack.EMPTY;
	private ItemStack lastStack;
	private ItemVariant variant = ItemVariant.blank();

	public ItemStackHandlerSlot(int index, ItemStackHandler handler, ItemStack initial) {
		this.index = index;
		this.handler = handler;
		this.lastStack = initial;
		this.setStack(initial);
		handler.initSlot(this);
	}

	@Override
	protected boolean canInsert(ItemVariant itemVariant) {
		return handler.isItemValid(this.index, itemVariant);
	}

	@Override
	public int getCapacity(ItemVariant itemVariant) {
		return this.handler.getStackLimit(this.index, itemVariant);
	}

	@Override
	public ItemStack getStack() {
		return this.stack;
	}

	@Override
	public void setStack(ItemStack stack) {
		this.stack = stack;
		this.variant = ItemVariant.of(stack);
	}

	public void setNewStack(ItemStack stack) {
		this.setStack(stack);
		this.onFinalCommit();
	}
	public void setNewStackInternal(ItemStack stack) {
		this.setStack(stack);
		this.handler.onStackChange(this, this.lastStack, this.stack);
		this.lastStack = this.stack;
	}

	@Override
	public ItemVariant getResource() {
		return this.variant;
	}

	public int getIndex() {
		return this.index;
	}

	@Override
	protected void onFinalCommit() {
		this.handler.onStackChange(this, this.lastStack, this.stack);
		this.lastStack = this.stack;
		this.handler.onContentsChanged(this.index);
	}
}
