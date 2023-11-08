package io.github.fabricators_of_create.porting_lib_build;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map.Entry;

public class ExpandFmjTask extends DefaultTask {
	public static final String FMJ = "fabric.mod.json";
	// at src/main/resources/template.fabric.mod.json, this is where it goes after processResources
	public static final String TEMPLATE_FMJ = "build/resources/main/template.fabric.mod.json";

	@TaskAction
	public void expandFmj() {
		File resources = getInputs().getFiles().getSingleFile();

		File fmj = null;
		for (File file : resources.listFiles()) {
			if (FMJ.equals(file.getName())) {
				fmj = file;
				break;
			}
		}
		if (fmj != null) {
			expandFmj(fmj);
		}
	}

	public void expandFmj(File fmjFile) {
		// load from template, add additional from project
		JsonObject template = makeDefaults();
		JsonObject moduleFmj = readModuleFmj(fmjFile);
		for (Entry<String, JsonElement> entry : moduleFmj.entrySet()) {
			String key = entry.getKey();
			JsonElement value = entry.getValue();
			tryMerge(template, key, value);
		}

		String name = "porting_lib_" + getProject().getName();
		Path parent = fmjFile.toPath().getParent();

		// fill in mixins
		String mixinsFileName = name + ".mixins.json";
		Path mixins = parent.resolve(mixinsFileName);
		if (Files.exists(mixins)) {
			JsonArray array = new JsonArray();
			array.add(mixinsFileName);
			template.add("mixins", array);
		}
		// and AW
		String awFileName = name + ".accesswidener";
		Path aw = parent.resolve(awFileName);
		if (Files.exists(aw)) {
			template.addProperty("accessWidener", awFileName);
		}

		// output
		try (FileOutputStream out = new FileOutputStream(fmjFile)) {
			out.write(PortingLibBuildPlugin.GSON.toJson(template).getBytes());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void tryMerge(JsonObject root, String key, JsonElement value) {
		JsonElement existing = root.get(key);
		if (existing == null) {
			root.add(key, value);
			return;
		}
		if (value.getClass() != existing.getClass()) {
			// no way to merge, just overwrite
			root.add(key, value);
			return;
		}
		if (value instanceof JsonObject obj) {
			mergeObj(root, key, (JsonObject) existing, obj);
		} else if (value instanceof JsonArray array) {
			mergeArray(root, key, (JsonArray) existing, array);
		} else {
			// overwrite existing
			root.add(key, value);
		}
	}

	public void mergeObj(JsonObject json, String key, JsonObject existing, JsonObject toAdd) {
		for (Entry<String, JsonElement> entry : toAdd.entrySet()) {
			String entryKey = entry.getKey();
			JsonElement value = entry.getValue();
			tryMerge(existing, entryKey, value);
		}
		json.add(key, existing);
	}

	public void mergeArray(JsonObject json, String key, JsonArray existing, JsonArray toAdd) {
		for (JsonElement element : toAdd) {
			existing.add(element);
		}
		json.add(key, existing);
	}

	public JsonObject makeDefaults() {
		File templateFmj = getProject().getRootProject().file(TEMPLATE_FMJ);
		return jsonFromFile(templateFmj);
	}

	public JsonObject readModuleFmj(File fmj) {
		return jsonFromFile(fmj);
	}

	public static JsonObject jsonFromFile(File file) {
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			return JsonParser.parseReader(reader).getAsJsonObject();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
