package me.lovesasuna.bot;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;

/**
 * @author LovesAsuna
 * @date 2020/7/12 19:04
 */

public final class Agent {
    public static void addToClassPath(Path paperJar) {
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        if (!(loader instanceof URLClassLoader)) {
            throw new RuntimeException("System ClassLoader is not URLClassLoader");
        } else {
            try {
                Method addURL = getAddMethod(loader);
                if (addURL == null) {
                    System.err.println("Unable to find method to add jar to System ClassLoader");
                    System.exit(1);
                }

                addURL.setAccessible(true);
                addURL.invoke(loader, paperJar.toUri().toURL());
            } catch (InvocationTargetException | MalformedURLException | IllegalAccessException e) {
                System.err.println("Unable to add Jar to System ClassLoader");
                e.printStackTrace();
                System.exit(1);
            }

        }
    }

    private static Method getAddMethod(Object o) {
        Class<?> clazz = o.getClass();
        Method method = null;

        while(method == null) {
            try {
                method = clazz.getDeclaredMethod("addURL", URL.class);
            } catch (NoSuchMethodException e) {
                clazz = clazz.getSuperclass();
                if (clazz == null) {
                    return null;
                }
            }
        }

        return method;
    }
}

