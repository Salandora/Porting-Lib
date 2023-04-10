package io.github.fabricators_of_create.porting_lib.transfer.item;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.function.Predicate;

/**
 * Wraps an ItemStackHandler in a Container for use in recipes and crafting.
 * @deprecated use of this class is discouraged, ItemStackHandlerContainer should fit all use cases.
 */
@Deprecated
public class RecipeWrapper extends ItemStackHandler implements Container {
	protected final ItemStackHandler handler;

	public RecipeWrapper(ItemStackHandler handler) {
		super(0);
		this.handler = handler;
	}

	@Override
	public int getContainerSize() {
		return handler.getSlots();
	}

	@Override
	public boolean isEmpty() {
		return handler.nonEmptySlots.isEmpty();
	}

	@Override
	@NotNull
	public ItemStack getItem(int index) {
		return handler.getStackInSlot(index);
	}

	@Override
	@NotNull
	public ItemStack removeItem(int index, int count) {
		if (index >= 0 && index < handler.getSlots()) {
			ItemStack current = handler.getStackInSlot(index);
			if (current.isEmpty())
				return ItemStack.EMPTY;
			current = current.copy();
			ItemStack extracted = current.split(count);
			handler.setStackInSlot(index, current);
			return extracted;
		}
		return ItemStack.EMPTY;
	}

	@Override
	@NotNull
	public ItemStack removeItemNoUpdate(int index) {
		return removeItem(index, Integer.MAX_VALUE);
	}

	@Override
	public void setItem(int index, @NotNull ItemStack stack) {
		handler.setStackInSlot(index, stack);
	}

	@Override
	public void clearContent() {
		handler.setSize(handler.getSlots());
	}

	@Override
	public int getMaxStackSize() { return 0; }
	@Override
	public void setChanged() {}
	@Override
	public boolean stillValid(@NotNull Player player) { return false; }
	@Override
	public void startOpen(@NotNull Player player) {}
	@Override
	public void stopOpen(@NotNull Player player) {}

	@Override
	public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		return handler.insert(resource, maxAmount, transaction);
	}

	@Override
	public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		return handler.extract(resource, maxAmount, transaction);
	}

	@Override
	@Nullable
	public ResourceAmount<ItemVariant> extractMatching(Predicate<ItemVariant> predicate, long maxAmount, TransactionContext transaction) {
		return handler.extractMatching(predicate, maxAmount, transaction);
	}

	@Override
	public Iterator<StorageView<ItemVariant>> iterator(TransactionContext transaction) {
		return handler.iterator(transaction);
	}

	@Override
	public Iterator<? extends StorageView<ItemVariant>> nonEmptyViews() {
		return handler.nonEmptyViews();
	}

	@Override
	public Iterable<? extends StorageView<ItemVariant>> nonEmptyIterable() {
		return handler.nonEmptyIterable();
	}

	@Override
	public int getSlots() {
		return handler.getSlots();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return handler.getStackInSlot(slot);
	}

	@Override
	public ItemVariant getVariantInSlot(int slot) {
		return handler.getVariantInSlot(slot);
	}

	@Override
	public int getSlotLimit(int slot) {
		return handler.getSlotLimit(slot);
	}

	@Override
	protected int getStackLimit(int slot, ItemVariant resource) {
		return handler.getStackLimit(slot, resource);
	}

	@Override
	public boolean isItemValid(int slot, ItemVariant resource) {
		return handler.isItemValid(slot, resource);
	}

	@Override
	protected void onLoad() {
		handler.onLoad();
	}

	@Override
	public void setSize(int size) {
		handler.setSize(size);
	}

	@Override
	public CompoundTag serializeNBT() {
		return handler.serializeNBT();
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		handler.deserializeNBT(nbt);
	}

	@Override
	public long simulateInsert(ItemVariant resource, long maxAmount, @Nullable TransactionContext transaction) {
		return handler.simulateInsert(resource, maxAmount, transaction);
	}

	@Override
	public long simulateExtract(ItemVariant resource, long maxAmount, @Nullable TransactionContext transaction) {
		return handler.simulateExtract(resource, maxAmount, transaction);
	}

	@Override
	public boolean supportsExtraction() {
		return handler.supportsExtraction();
	}

	@Override
	public boolean supportsInsertion() {
		return handler.supportsInsertion();
	}

	@Override
	public Iterable<? extends StorageView<ItemVariant>> iterable(TransactionContext transaction) {
		return handler.iterable(transaction);
	}

	@Override
	public long getVersion() {
		return handler.getVersion();
	}

	@Override
	public @Nullable StorageView<ItemVariant> exactView(TransactionContext transaction, ItemVariant resource) {
		return handler.exactView(transaction, resource);
	}

	@Override
	public String toString() {
		return "RecipeWrapper{" + handler + "}";
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		handler.setStackInSlot(slot, stack);
	}

	@Override
	protected void onContentsChanged(ItemStackHandlerSlot slot) {
		handler.onContentsChanged(slot);
	}

	@Override
	public ItemStackHandlerSlot getSlot(int index) {
		return handler.getSlot(index);
	}

	@Nullable
	@Override
	public ResourceAmount<ItemVariant> extractAny(long maxAmount, TransactionContext transaction) {
		return handler.extractAny(maxAmount, transaction);
	}

	@Override
	@SuppressWarnings("deprecation")
	public void onContentsChanged(int slot) {
		handler.onContentsChanged(slot);
	}
}
