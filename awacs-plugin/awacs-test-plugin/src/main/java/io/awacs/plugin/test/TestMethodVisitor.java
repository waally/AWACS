package io.awacs.plugin.test;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by wangli on 2016/10/26.
 */
public class TestMethodVisitor  extends MethodVisitor implements Opcodes {


    private String className;

    public TestMethodVisitor(int i, MethodVisitor methodVisitor,String className) {
        super(i, methodVisitor);
        this.className = className;
    }

    @Override
    public void visitCode() {
        super.visitCode();
        mv.visitMethodInsn(INVOKESTATIC, "io/awacs/plugin/test/TestPlugin", "beginTime", "()V",false);
    }

    @Override
    public void visitInsn(int i) {
        if(i == IRETURN || i == RETURN|| i == ARETURN || i == ATHROW){
            mv.visitLdcInsn(className);
            mv.visitMethodInsn(INVOKESTATIC, "io/awacs/plugin/test/TestPlugin", "endTime", "(Ljava/lang/String;)V", false);
        }
        super.visitInsn(i);
    }

}
