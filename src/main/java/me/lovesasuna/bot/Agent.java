package me.lovesasuna.bot;

import sun.misc.Unsafe;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;

/**
 * @author LovesAsuna
 * @date 2020/7/12 19:04
 */

public final class Agent {
    private static final Unsafe UNSAFE;
    private static final MethodHandles.Lookup LOOKUP;
    private static final MethodType methodType;
    private static final ClassLoader loader;

    static {
        try {
            loader = ClassLoader.getSystemClassLoader();
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            UNSAFE = (Unsafe) theUnsafe.get(null);
            methodType = MethodType.methodType(void.class, URL.class);
            Field lookupField = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            Object lookupBase = UNSAFE.staticFieldBase(lookupField);
            long lookupOffset = UNSAFE.staticFieldOffset(lookupField);
            LOOKUP = (MethodHandles.Lookup) UNSAFE.getObject(lookupBase, lookupOffset);
        } catch (Throwable t) {
            throw new IllegalStateException("Unsafe not found");
        }
    }

    public static void addToClassPath(Path jarPath) {
        try {
            if (loader instanceof URLClassLoader) {
                MethodHandle methodHandle = LOOKUP.findVirtual(loader.getClass(), "addURL", methodType);
                methodHandle.invoke(loader, jarPath.toUri().toURL());
            } else {
                Field ucpField = loader.getClass().getDeclaredField("ucp");
                long ucpOffset = UNSAFE.objectFieldOffset(ucpField);
                Object ucp = UNSAFE.getObject(loader, ucpOffset);
                MethodHandle methodHandle = LOOKUP.findVirtual(ucp.getClass(), "addURL", methodType);
                methodHandle.invoke(ucp, jarPath.toUri().toURL());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}

