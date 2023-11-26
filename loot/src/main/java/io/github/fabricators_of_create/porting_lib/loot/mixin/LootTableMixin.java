package io.github.fabricators_of_create.porting_lib.loot.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.fabricators_of_create.porting_lib.loot.LootCollector;
import io.github.fabricators_of_create.porting_lib.loot.extensions.LootTableBuilderExtensions;
import io.github.fabricators_of_create.porting_lib.loot.extensions.LootTableExtensions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Objects;
import java.util.function.Consumer;

import static net.minecraft.world.level.storage.loot.LootTable.createStackSplitter;

@Mixin(LootTable.class)
public abstract class LootTableMixin implements LootTableExtensions {
	@Shadow
	public abstract ObjectArrayList<ItemStack> getRandomItems(LootContext context);

	@Shadow
	public abstract void getRandomItemsRaw(LootContext context, Consumer<ItemStack> lootConsumer);

	@Unique
	private ResourceLocation lootTableId;

	@Override
	public void setLootTableId(final ResourceLocation id) {
		if (this.lootTableId != null) {
			throw new IllegalStateException("Attempted to rename loot table from '" + this.lootTableId + "' to '" + id + "': this is not supported");
		}
		this.lootTableId = Objects.requireNonNull(id);
	}

	@Override
	public ResourceLocation getLootTableId() {
		return this.lootTableId;
	}

	@Inject(method = "getRandomItems(Lnet/minecraft/world/level/storage/loot/LootContext;Ljava/util/function/Consumer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/LootTable;getRandomItemsRaw(Lnet/minecraft/world/level/storage/loot/LootContext;Ljava/util/function/Consumer;)V"), cancellable = true)
	private void portingLib$getRandomItems(LootContext context, Consumer<ItemStack> lootConsumer, CallbackInfo ci) {
		this.getRandomItems(context).forEach(lootConsumer);
		ci.cancel();
	}

	@Redirect(method = "getRandomItems(Lnet/minecraft/world/level/storage/loot/LootContext;)Lit/unimi/dsi/fastutil/objects/ObjectArrayList;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/LootTable;getRandomItems(Lnet/minecraft/world/level/storage/loot/LootContext;Ljava/util/function/Consumer;)V"))
	private void portingLib$getRandomItems(LootTable instance, LootContext context, Consumer<ItemStack> lootConsumer) {
		LootCollector collector = new LootCollector(createStackSplitter(context, lootConsumer));
		this.getRandomItemsRaw(context, collector);
		collector.finish(this.getLootTableId(), context);
	}

	@Mixin({LootTable.Builder.class})
	public static class BuilderMixin implements LootTableBuilderExtensions {
		@Unique
		private ResourceLocation id;

		public BuilderMixin() {
		}

		public void port_lib$setId(ResourceLocation id) {
			this.id = id;
		}

		@ModifyReturnValue(
				method = {"build"},
				at = {@At("RETURN")}
		)
		private LootTable addId(LootTable table) {
			if (this.id != null) {
				table.setLootTableId(this.id);
			}

			return table;
		}
	}
}
