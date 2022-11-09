package io.github.fabricators_of_create.porting_lib.event.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;

/**
 * @deprecated move to {@link InteractEvents#USE}
 */
@Deprecated(forRemoval = true)
@Environment(EnvType.CLIENT)
public interface OnStartUseItemCallback {
	/**
	 * SUCCESS - swing hand
	 * FAIL - cancel, do nothing
	 * PASS - do nothing
	 */
	Event<OnStartUseItemCallback> EVENT = EventFactory.createArrayBacked(OnStartUseItemCallback.class, callbacks -> (hand) -> {
		for (OnStartUseItemCallback callback : callbacks) {
			InteractionResult result = callback.onStartUse(hand);
			if (result != InteractionResult.PASS) return result;
		}
		return InteractionResult.PASS;
	});

	InteractionResult onStartUse(InteractionHand hand);
}
