package io.github.fabricators_of_create.porting_lib.extensions.mixin.common;

import io.github.fabricators_of_create.porting_lib.extensions.extensions.ItemExtensions;
import io.github.fabricators_of_create.porting_lib.util.ToolAction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Item.class)
public abstract class ItemMixin implements ItemExtensions {
    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return false;
    }
}
