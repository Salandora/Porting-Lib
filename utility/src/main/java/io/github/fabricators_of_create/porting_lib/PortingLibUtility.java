package io.github.fabricators_of_create.porting_lib;

import io.github.fabricators_of_create.porting_lib.util.ServerLifecycleHooks;
import net.fabricmc.api.ModInitializer;

public class PortingLibUtility implements ModInitializer {
	@Override
	public void onInitialize() {
		ServerLifecycleHooks.init();
	}
}
