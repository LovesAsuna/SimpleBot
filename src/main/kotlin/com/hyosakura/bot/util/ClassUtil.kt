package com.hyosakura.bot.util

import java.io.File
import java.io.IOException
import java.net.JarURLConnection
import java.net.URL
import java.net.URLDecoder
import java.util.*
import java.util.jar.JarFile

class ClassUtil {
    private val jarFile = JarFile(com.hyosakura.bot.Main::class.java.protectionDomain.codeSource.location.path)

    @Deprecated("不建议使用")
    fun getFunctions(): List<String> {
        val jarEntry = jarFile.entries()
        val functionList = arrayListOf<String>()
        while (jarEntry.hasMoreElements()) {
            val file = jarEntry.nextElement()
            if (file.name.contains(Regex("me/lovesasuna/bot/controller/\\w+.class"))) {
                functionList.add(file.name.split("/")[4].split(".")[0])
            }
        }
        return functionList
    }

    companion object {
        @JvmStatic
        fun getClasses(
            packageName: String,
            classLoader: ClassLoader = Thread.currentThread().contextClassLoader,
            annotationClass: Class<out Annotation>? = null
        ): Set<Class<*>> {
            var packageNameClone = packageName
            val classes: MutableSet<Class<*>> = HashSet()
            val recursive = true
            val packageDirName = packageNameClone.replace('.', '/')
            val dirs: Enumeration<URL>
            try {
                dirs = classLoader.getResources(packageDirName)
                while (dirs.hasMoreElements()) {
                    val url = dirs.nextElement() as URL
                    val protocol: String = url.protocol
                    if ("file" == protocol) {
                        val filePath = URLDecoder.decode(url.file, "UTF-8")
                        findAndAddClassesInPackageByFile(packageNameClone, filePath, recursive, classes)
                    } else if ("jar" == protocol) {
                        var jar: JarFile
                        try {
                            jar = (url.openConnection() as JarURLConnection).jarFile
                            val entries = jar.entries()
                            while (entries.hasMoreElements()) {
                                val entry = entries.nextElement()
                                var name = entry.name
                                if (name[0] == '/') {
                                    name = name.substring(1)
                                }
                                if (name.startsWith(packageDirName)) {
                                    val idx = name.lastIndexOf('/')
                                    if (idx != -1) {
                                        packageNameClone = name.substring(0, idx).replace('/', '.')
                                    }
                                    if (idx != -1 || recursive) {
                                        if (name.endsWith(".class") && !entry.isDirectory) {
                                            val className = name.substring(packageNameClone.length + 1, name.length - 6)
                                            try {
                                                classes.add(classLoader.loadClass("$packageNameClone.$className"))
                                            } catch (e: Throwable) {
                                                com.hyosakura.bot.Main.logger.info(
                                                    "Error occurs while adding class [$packageNameClone.$className]"
                                                )
                                                e.printStackTrace()
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return if (annotationClass != null) {
                classes.filter { c ->
                    c.getAnnotation(annotationClass) != null
                }.toSet()
            } else classes
        }

        /**
         * 以文件的形式来获取包下的所有Class
         *
         * @param packageName 包名
         * @param packagePath 包路径
         * @param recursive 迭代
         * @param classes 类集合
         */
        private fun findAndAddClassesInPackageByFile(
            packageName: String,
            packagePath: String,
            recursive: Boolean,
            classes: MutableSet<Class<*>>
        ) {
            val dir = File(packagePath)
            if (!dir.exists() || !dir.isDirectory) {
                return
            }
            val dirFiles = dir.listFiles { file -> (recursive && file.isDirectory || file.name.endsWith(".class")) }
            for (file in dirFiles) {
                if (file.isDirectory) {
                    findAndAddClassesInPackageByFile(
                        packageName + "." + file.name,
                        file.absolutePath,
                        recursive,
                        classes
                    )
                } else {
                    val className = file.name.substring(0, file.name.length - 6)
                    try {
                        classes.add(Thread.currentThread().contextClassLoader.loadClass("$packageName.$className"))
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }
                }
            }
        }

        /**
         * 返回该类实现的接口和直接继承的类
         * @param c 类
         * @return 该类实现的接口和直接继承的类
         */
        @JvmStatic
        fun getSuperClass(c: Class<*>): Set<Class<*>> {
            val classSet = HashSet<Class<*>>()
            val superClass = c.superclass
            val superInterface = c.interfaces
            if (superInterface.isNotEmpty()) {
                classSet.addAll(superInterface)
            }
            classSet.add(superClass)
            return classSet
        }
    }

}