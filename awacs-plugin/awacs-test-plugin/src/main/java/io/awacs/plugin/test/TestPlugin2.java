package io.awacs.plugin.test;

import io.awacs.agent.MessageHub;
import io.awacs.core.NoSuchKeyTypeException;
import io.awacs.core.Plugin;
import io.awacs.core.PluginDescriptor;
import io.awacs.core.transport.Key;
import io.awacs.core.transport.Message;
import io.awacs.core.util.LoggerPlus;
import io.awacs.core.util.LoggerPlusFactory;
import io.awacs.protocol.binary.BinaryMessage;
import io.awacs.protocol.binary.ByteKey;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

/**
 * Created by wangli on 2016/10/26.
 */
public class TestPlugin2 implements Plugin {

    private static LoggerPlus logger = LoggerPlusFactory.getLogger(TestPlugin2.class);

    private static PluginDescriptor descriptor;

    private static Key<?> key;

    private Instrumentation inst;

    private static ThreadLocal<TestReport2> asm_local_thread_info = new ThreadLocal<>();

    public static void beginTime(String className){
        System.out.println("test plugin method("+className+") enter");
        TestReport2 info = asm_local_thread_info.get();
        if(info==null){
            info = new TestReport2(System.currentTimeMillis(),className);
            asm_local_thread_info.set(info);
        }else{
            info = info.push(new TestReport2(System.currentTimeMillis(),className));
            asm_local_thread_info.set(info);
        }
    }

    public static void endTime(){
        System.out.print("test plugin method exit");
        TestReport2 info = asm_local_thread_info.get();
        if(info == null){
            return;
        }
        System.out.println(info);
        TestReport2 tmp = info.pop(System.currentTimeMillis());
        asm_local_thread_info.set(tmp);
        if(info.isRoot()){
            String content = info.toString();
            Message m = new BinaryMessage.BinaryMessageBuilder().setKey(key).setBody(content.getBytes()).build();
            System.out.println("send message :"+content);
            MessageHub.instance.publish(m);
        }
    }

    public static void handlerException(Exception e){
        endTime();
        System.out.println("捕获一个异常："+ e.getMessage());
    }

    @Override
    public PluginDescriptor getDescriptor() {
        return descriptor;
    }

    @Override
    public void setDescriptor(PluginDescriptor pluginDescriptor) {
        TestPlugin2.descriptor = pluginDescriptor;
        try {
            key = ByteKey.getKey(descriptor.getKeyClass(), descriptor.getKeyValue());
        } catch (NoSuchKeyTypeException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Instrumentation getInstrumentation() {
        return inst;
    }

    @Override
    public void setInstrumentation(Instrumentation instrumentation) {
        inst = instrumentation;
    }

    @Override
    public void boot() {
        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                try {
                    String packagepre = "com/wangli/study/";//(String) descriptor.getProperties().getOrDefault("filter-package","com/wangli/study/");
    //                if(!className.startsWith("io/awacs/")&&!className.startsWith("java/")&&!className.startsWith("sun/")&&!className.startsWith("jdk/")
    //                    &&!className.startsWith("com/sun/")&&!className.startsWith("com/oracle/")
    //                    &&!className.startsWith("javax/")
    //                    &&!className.startsWith("oracle/")
    //                    &&!className.startsWith("javafx/")
    //                    &&!className.startsWith("netscape/")
    //                    &&!className.startsWith("org/objectweb/asm")&&!className.startsWith("io/netty/")
    //                    &&!className.startsWith("com/google")&&!className.startsWith("com/alibaba/fastjson")){
                    if(className.startsWith(packagepre)){
                        System.out.println("test plugin aop class : "+className);

                        ClassReader cr = new ClassReader(classfileBuffer);
                        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
                        cr.accept(new TestClassVisitor2(Opcodes.ASM4, cw), 0);
                        return cw.toByteArray();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    return classfileBuffer;
                }
                return classfileBuffer;
            }
        });
    }
}
