package io.github.fabricators_of_create.porting_lib.tool.mixin;

import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib.tool.ToolAction;
import io.github.fabricators_of_create.porting_lib.tool.ToolActions;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.Block;

@Mixin(PickaxeItem.class)
public class PickaxeItemMixin extends DiggerItem {
    public PickaxeItemMixin(float toolBaseDamage, float attackSpeed, Tier material, TagKey<Block> effectiveBlocks, Properties settings) {
        super(toolBaseDamage, attackSpeed, material, effectiveBlocks, settings);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return ToolActions.DEFAULT_PICKAXE_ACTIONS.contains(toolAction);
    }
}
