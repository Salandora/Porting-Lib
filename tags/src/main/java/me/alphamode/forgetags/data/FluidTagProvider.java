package me.alphamode.forgetags.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class FluidTagProvider extends FabricTagProvider.FluidTagProvider {
	public FluidTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
		super(output, completableFuture);
	}

	@Override
	protected void addTags(HolderLookup.Provider arg) {
//        if(BuiltInRegistries.FLUID.get(new ResourceLocation("c:milk")) != null) {
//            getOrCreateTagBuilder(Tags.Fluids.MILK).add(BuiltInRegistries.FLUID.get(new ResourceLocation("c:milk")));
//        }
	}
}
