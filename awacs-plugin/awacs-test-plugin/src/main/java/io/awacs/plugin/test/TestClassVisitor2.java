package io.awacs.plugin.test;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by Administrator on 2016/10/17.
 */
public class TestClassVisitor2 extends ClassVisitor implements Opcodes {

    private boolean isInterface;

    private String className;

    public TestClassVisitor2(int i, ClassWriter cw) {
        super(i,cw);
    }

    @Override
    public void visit(int i, int i1, String s, String s1, String s2, String[] strings) {
        isInterface = (i & ACC_INTERFACE) != 0;
        this.className = s;
        super.visit(i, i1, s, s1, s2, strings);
    }

    @Override
    public MethodVisitor visitMethod(int i, String s, String s1, String s2, String[] strings) {
        MethodVisitor mv = super.visitMethod(i, s, s1, s2, strings);
        if(!isInterface&&mv!=null&&!s.equals("<init>")&&!s.equals("<clinit>")){
            System.out.println("test plugin aop method : "+ s + s1);
            return new TestMethodVisitor2(ASM4,mv,className+"."+s);
        }
        return mv;
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }
}
