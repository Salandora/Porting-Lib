package io.github.fabricators_of_create.porting_lib.transfer.item;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;

import java.util.Iterator;
import org.jetbrains.annotations.NotNull;

public interface SlottedStackStorage extends SlottedStorage<ItemVariant> {
	ItemStack getStackInSlot(int slot);

	void setStackInSlot(int slot, @NotNull ItemStack stack);

	int getSlotLimit(int slot);

	default boolean isItemValid(int slot, ItemVariant resource) {
		return true;
	}

	default long insertSlot(int slot, ItemVariant resource, long maxAmount, TransactionContext ctx) {
		return getSlot(slot).insert(resource, maxAmount, ctx);
	}

	default long extractSlot(int slot, ItemVariant resource, long maxAmount, TransactionContext ctx) {
		return getSlot(slot).extract(resource, maxAmount, ctx);
	}

	@Override
	default Iterator<StorageView<ItemVariant>> iterator() {
		//noinspection unchecked,rawtypes
		return (Iterator) getSlots().iterator();
	}
}
