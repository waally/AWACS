package io.awacs.plugin.test;

import io.awacs.core.util.LoggerPlus;
import io.awacs.core.util.LoggerPlusFactory;
import org.objectweb.asm.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by wangli on 2016/10/26.
 */
public class TestClassVisitor extends ClassVisitor implements Opcodes {


    /**是否为接口**/
    private boolean isInterface =false;
    /**是否含有spring的Controller注解**/
    private boolean haveAnnotation = false;
    /**类名**/
    private String className;

    public TestClassVisitor(int i, ClassVisitor classVisitor) {
        super(i, classVisitor);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        if("Lorg/springframework/stereotype/Controller;".equals(desc))
            haveAnnotation = true;
        return super.visitAnnotation(desc, visible);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        isInterface = (access & ACC_INTERFACE) != 0;
        className = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signture, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signture, exceptions);
        if(!isInterface&&mv!=null&&!name.equals("<init>")&&haveAnnotation&&name.startsWith("handle"))
            return new TestMethodVisitor(ASM5,mv,className);
        return mv;
    }


    public static void main(String[] args) throws IOException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ClassReader cr = new ClassReader(TestClassVisitor.class.getClassLoader().getResourceAsStream("io.awacs.plugin.test.HelloController".replace('.', '/') + ".class"));
        cr.accept(new TestClassVisitor(ASM5,cw), 0);
        byte[] data = cw.toByteArray();
        File file = new File("D://HelloController.class");
        FileOutputStream fout = new FileOutputStream(file);
        fout.write(data);
        fout.close();
        Class clazz = new MyClassLoader().defineClass("io.awacs.plugin.test.HelloController", data);
        Method method = clazz.getMethod("handleTest");
//        Method method = clazz.getMethod("handleTest",String.class);
        method.invoke(clazz.newInstance());
    }

    static class MyClassLoader extends ClassLoader {
        public Class defineClass(String name, byte[] b) {
            return defineClass(name, b, 0, b.length);
        }
    }
}
