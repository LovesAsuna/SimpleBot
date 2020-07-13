package me.lovesasuna.bot;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.nio.file.Path;
import java.util.jar.JarFile;

/**
 * @author LovesAsuna
 * @date 2020/7/12 22:42
 */

public final class Agent {
    private static Instrumentation inst = null;

    public static void agentmain(String agentArgs, Instrumentation inst) {
        Agent.inst = inst;
    }

    public static void addToClassPath(Path paperJar) {
        if (inst == null) {
            System.err.println("Unable to retrieve Instrumentation API to add jar to ClassPath");
            System.exit(1);
        } else {
            try {
                inst.appendToSystemClassLoaderSearch(new JarFile(paperJar.toFile()));
            } catch (IOException e) {
                System.err.println("Failed to add jar to ClassPath");
                e.printStackTrace();
                System.exit(1);
            }
        }

    }
}

