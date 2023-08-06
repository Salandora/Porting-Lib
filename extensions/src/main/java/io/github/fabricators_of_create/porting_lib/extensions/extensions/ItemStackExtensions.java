package io.github.fabricators_of_create.porting_lib.extensions.extensions;

import io.github.fabricators_of_create.porting_lib.util.ToolAction;

public interface ItemStackExtensions {
	default boolean canPerformAction(ToolAction toolAction) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
