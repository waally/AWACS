package io.awacs.plugin.test;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by Administrator on 2016/10/17.
 */
public class TestMethodVisitor2 extends MethodVisitor implements Opcodes {

    private Label begin,finish,excp;

    private String className;

    public TestMethodVisitor2(int asm4, MethodVisitor mv,String className) {
        super(asm4,mv);
        this.className = className;
    }


    @Override
    public void visitCode() {
        super.visitCode();
        begin = new Label();
        mv.visitLabel(begin);
        mv.visitLdcInsn(className);
        mv.visitMethodInsn(INVOKESTATIC, "io/awacs/plugin/test/TestPlugin2", "beginTime", "(Ljava/lang/String;)V",false);
    }

    @Override
    public void visitInsn(int i) {

        if(i == IRETURN || i == RETURN || i == LRETURN|| i == FRETURN|| i ==  DRETURN || i == ARETURN){
            mv.visitMethodInsn(INVOKESTATIC, "io/awacs/plugin/test/TestPlugin2", "endTime", "()V", false);
            super.visitInsn(i);
        }else{
            super.visitInsn(i);
        }


    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }

    @Override
    public void visitMaxs(int i, int i1) {
        finish = new Label();
        mv.visitLabel(finish);
        excp = new Label();
        mv.visitLabel(excp);
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESTATIC,"io/awacs/plugin/test/TestPlugin2","handlerException","(Ljava/lang/Exception;)V",false);
        mv.visitInsn(ATHROW);
        mv.visitTryCatchBlock(begin,finish,excp,"java/lang/Exception");
        super.visitMaxs(i, i1);
    }
}
