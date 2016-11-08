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
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangli on 2016/10/26.
 */
public class TestPlugin implements Plugin {

    private static LoggerPlus logger = LoggerPlusFactory.getLogger(TestPlugin.class);

    private static PluginDescriptor descriptor;

    private static Key<?> key;

    private static ThreadLocal<Long> threadLocal = new ThreadLocal<Long>();

    private Instrumentation inst;

    public static void beginTime(){
        Long time = threadLocal.get();
        if(time==null){
            time = System.currentTimeMillis();
            threadLocal.set(time);
        }
    }

    public static void endTime(String className){
        Long time = threadLocal.get();
        long duration = System.currentTimeMillis()-time;
        System.out.println(time+className);
        String content = new TestReport().setService(className).setTime(duration).toString();
        Message m = new BinaryMessage.BinaryMessageBuilder().setKey(key).setBody(content.getBytes()).build();
        System.out.println("send message :"+content);
        MessageHub.instance.publish(m);
    }

    @Override
    public PluginDescriptor getDescriptor() {
        return descriptor;
    }

    @Override
    public void setDescriptor(PluginDescriptor pluginDescriptor) {
        TestPlugin.descriptor = pluginDescriptor;
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
                ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                ClassReader cr = new ClassReader(classfileBuffer);
                cr.accept(new TestClassVisitor(Opcodes.ASM5,cw), 0);
                return cw.toByteArray();
            }
        });
    }
}
