package io.github.fabricators_of_create.porting_lib;

import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PortingConstants {
	public static final Logger LOGGER = LoggerFactory.getLogger("porting-lib.models");

	public static ResourceLocation id(String path) {
		return new ResourceLocation("porting-lib", path);
	}
}
