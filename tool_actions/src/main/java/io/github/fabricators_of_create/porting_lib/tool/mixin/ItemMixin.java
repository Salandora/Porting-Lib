package io.github.fabricators_of_create.porting_lib.tool.mixin;

import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib.tool.ToolAction;
import io.github.fabricators_of_create.porting_lib.tool.extensions.ItemExtensions;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

@Mixin(Item.class)
public abstract class ItemMixin implements ItemExtensions {
    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return false;
    }
}
