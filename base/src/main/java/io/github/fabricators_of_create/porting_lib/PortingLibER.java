package io.github.fabricators_of_create.porting_lib;

import com.google.common.collect.ImmutableList;

import io.github.fabricators_of_create.porting_lib.asm.ASMUtils;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;

import com.chocohead.mm.api.ClassTinkerers;

import org.objectweb.asm.tree.MethodNode;

import java.util.List;

public class PortingLibER implements Runnable {
	private static final List<String> PATCHED_CLASSES = mapClasses(
			"class_6329",
			"class_3138",
			"class_1299",
			"class_1419",
			"class_7110",
			"class_1505",
			"class_1564$class_1567",
			"class_1628",
			"class_4985",
			"class_1642",
			"class_1641",
			"class_4274",
			"class_1646",
			"class_3765",
			"class_1948",
			"class_3769",
			"class_2910",
			"class_3366$class_3384",
			"class_3409$class_3410",
			"class_3447",
			"class_3471$class_3480",
			"class_3499"
	);

	private static final String FINALIZE_SPAWN = ASMUtils.mapM("class_1308.method_5943(Lnet/minecraft/class_5425;Lnet/minecraft/class_1266;Lnet/minecraft/class_3730;Lnet/minecraft/class_1315;Lnet/minecraft/class_2487;)Lnet/minecraft/class_1315;");
	private static final String FINALIZE_SPAWN_DESC = "(L" + mapClassDot("class_5425") + ";L" + mapClassDot("class_1266") + ";L" + mapClassDot("class_3730") + ";L" + mapClassDot("class_1315") + ";L" + mapClassDot("class_2487") + ";)L" + mapClassDot("class_1315") + ";";
	private static final String PATCHED_DESC = "(L" + mapClassDot("class_1308") + ";L" + mapClassDot("class_5425") + ";L" + mapClassDot("class_1266") + ";L" + mapClassDot("class_3730") + ";L" + mapClassDot("class_1315") + ";L" + mapClassDot("class_2487") + ";)L" + mapClassDot("class_1315") + ";";

	@Override
	public void run() {
		PATCHED_CLASSES.forEach(s -> ClassTinkerers.addTransformation(s, PortingLibER::replaceFinalizeSpawn));
	}

	private static void replaceFinalizeSpawn(ClassNode classNode) {
		for (MethodNode method : classNode.methods) {
			InsnList instructions = method.instructions;
			for (int i = 0; i < instructions.size(); i++) {
				AbstractInsnNode node = instructions.get(i);
				if (node instanceof MethodInsnNode methodNode) {
					boolean replace = methodNode.getOpcode() == Opcodes.INVOKEVIRTUAL
							&& methodNode.name.equals(FINALIZE_SPAWN)
							&& methodNode.desc.equals(FINALIZE_SPAWN_DESC);
					if (replace) {
						MethodInsnNode redirect = new MethodInsnNode(
								Opcodes.INVOKESTATIC,
								"io/github/fabricators_of_create/porting_lib/util/PortingHooks",
								"onFinalizeSpawn",
								PATCHED_DESC,
								false
						);
						instructions.set(node, redirect);
					}
				}
			}
		}
	}

	private static List<String> mapClasses(String... classes) {
		ImmutableList.Builder<String> mappedClasses = ImmutableList.builder();
		for (String clazz : classes)
			mappedClasses.add(ASMUtils.mapC(clazz));
		return mappedClasses.build();
	}

	private static String mapClassDot(String name) {
		return ASMUtils.mapC(name).replace('.', '/');
	}
}
