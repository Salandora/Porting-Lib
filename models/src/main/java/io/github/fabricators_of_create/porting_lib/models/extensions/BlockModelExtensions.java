package io.github.fabricators_of_create.porting_lib.models.extensions;

import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface BlockModelExtensions {
	void port_lib$setRenderMaterial(RenderMaterial material);
}
