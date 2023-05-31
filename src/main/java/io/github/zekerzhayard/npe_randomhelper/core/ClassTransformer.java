package io.github.zekerzhayard.npe_randomhelper.core;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class ClassTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if ("com.pixelmonmod.pixelmon.RandomHelper".equals(transformedName)) {
            ClassNode cn = new ClassNode();
            new ClassReader(basicClass).accept(cn, 0);
            for (MethodNode mn : cn.methods) {
                if (RemapUtils.checkMethodName(cn.name, mn.name, mn.desc, "getRandomElementFromCollection") && RemapUtils.checkMethodDesc(mn.desc, "(Ljava/util/Collection;)Ljava/lang/Object;")) {
                    InsnList il = new InsnList();
                    LabelNode ln = new LabelNode();
                    il.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    il.add(new JumpInsnNode(Opcodes.IFNONNULL, ln));
                    il.add(new InsnNode(Opcodes.ACONST_NULL));
                    il.add(new InsnNode(Opcodes.ARETURN));
                    il.add(ln);
                    il.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));
                    mn.instructions.insert(il);
                }
            }
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            cn.accept(cw);
            basicClass = cw.toByteArray();
        }
        return basicClass;
    }
}
