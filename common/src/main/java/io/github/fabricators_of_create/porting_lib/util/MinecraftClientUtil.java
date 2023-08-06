package io.github.fabricators_of_create.porting_lib.util;

import java.util.Locale;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;

@Environment(EnvType.CLIENT)
public final class MinecraftClientUtil {
	public static Locale getLocale() {
		String language = Minecraft.getInstance().getLanguageManager().getSelected();
		if (!language.contains("_")) { // Vanilla has some languages without underscores
			return new Locale(language);
		}

		String[] splitLangCode = language.split("_", 2);
		return new Locale(splitLangCode[0], splitLangCode[1]);
	}

	private MinecraftClientUtil() {}
}
