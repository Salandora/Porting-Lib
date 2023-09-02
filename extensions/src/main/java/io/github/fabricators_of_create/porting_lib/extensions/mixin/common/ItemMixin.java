package io.github.fabricators_of_create.porting_lib.extensions.mixin.common;

import io.github.fabricators_of_create.porting_lib.extensions.extensions.ItemExtensions;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Item.class)
public abstract class ItemMixin implements ItemExtensions {
}
