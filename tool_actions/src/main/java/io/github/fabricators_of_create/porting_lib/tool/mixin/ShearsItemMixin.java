package io.github.fabricators_of_create.porting_lib.tool.mixin;

import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib.tool.ToolAction;
import io.github.fabricators_of_create.porting_lib.tool.ToolActions;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;

@Mixin(ShearsItem.class)
public class ShearsItemMixin extends Item {
    public ShearsItemMixin(Properties settings) {
        super(settings);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return ToolActions.DEFAULT_SHEARS_ACTIONS.contains(toolAction);
    }
}
