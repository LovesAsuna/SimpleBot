package me.lovesasuna.bot.data

import me.lovesasuna.bot.util.network.DownloadUtil


interface DependenceData {
    val data: String

    interface DependenceMD5 : DependenceData

    interface DependenceUrl : DependenceData

    interface MavenUrl : DependenceUrl

    interface LanzousUrl : DependenceUrl

    enum class MD5(override val data: String) : DependenceMD5 {
        /*JackSon*/
        JACKSON_ANNOTATIONS("ae29476ef47b802516535a8fbd964ec2"),
        JACKSON_CORE("178171dcc707f3aa1b5d53717530a589"),
        JACKSON_DATABIND("5dc6f0ba9a79ffb5440b9561a55806ed"),
        JACKSON_MODULE("f14829f533c5e5459126f254f009d694"),

        /*Kotlin*/
        KOTLIN_STDLIB("7dea2e32e4b71f48d8863dbef721b408"),
        KOTLIN_STDLIB_ANNOTATIONS("4990efa6b740f88e0772f3b8b815ba03"),
        KOTLIN_STDLIB_COMMON("9c36600bc1179cd6b79a9eb51fefb238"),
        KOTLIN_STDLIB_JDK7("ec6da201a772809331172ed63ec0f3c0"),
        KOTLIN_STDLIB_JDK8("60ea8ff676c976e622f9ae14eac1751"),
        KOTLIN_REFLECT("bce6fbfa240eb95ad6a2e215ed7e6f31"),
        KOTLIN_SERIALIZATION("4ad926d7024253a6b2347b11b256828c"),

        /*Mirai*/
        CUSTOMCORE("aeda4b3e02190baaeade56c70b2b50fc"),

        /*Jsoup依赖*/
        JSOUP("5ee148bf2db7c6d81edb7904e970e9db"),

        /*DNSJava*/
        DNSJAVA("80ad3247ab61ee84245becc94e931c68"),
    }

    enum class Lanzous(override val data: String) : LanzousUrl {
        CUSTOMCORE(DownloadUtil.Lanzou.getlanzouUrl("iIVhAfu89he")),
    }

    enum class Maven(override val data: String) : MavenUrl {
        JACKSON_DATABIND("https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-databind/2.11.1/jackson-databind-2.11.1.jar"),
        JACKSON_MODULE("https://repo1.maven.org/maven2/com/fasterxml/jackson/module/jackson-module-kotlin/2.11.1/jackson-module-kotlin-2.11.1.jar"),
        JACKSON_CORE("https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-core/2.11.1/jackson-core-2.11.1.jar"),
        JACKSON_ANNOTATIONS("https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-annotations/2.11.1/jackson-annotations-2.11.1.jar"),

        KOTLIN_STDLIB("https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-stdlib/1.3.72/kotlin-stdlib-1.3.72.jar"),
        KOTLIN_STDLIB_ANNOTATIONS("https://repo1.maven.org/maven2/org/jetbrains/annotations/19.0.0/annotations-19.0.0.jar"),
        KOTLIN_STDLIB_COMMON("https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-stdlib-common/1.3.72/kotlin-stdlib-common-1.3.72.jar"),
        KOTLIN_STDLIB_JDK7("https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-stdlib-jdk7/1.3.72/kotlin-stdlib-jdk7-1.3.72.jar"),
        KOTLIN_STDLIB_JDK8("https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-stdlib-jdk8/1.3.72/kotlin-stdlib-jdk8-1.3.72.jar"),
        KOTLIN_REFLECT("https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-reflect/1.3.72/kotlin-reflect-1.3.72.jar"),
        KOTLIN_SERIALIZATION("https://repo1.maven.org/maven2/org/jetbrains/kotlinx/kotlinx-serialization-runtime/0.20.0/kotlinx-serialization-runtime-0.20.0.jar"),

        JSOUP("https://repo1.maven.org/maven2/org/jsoup/jsoup/1.13.1/jsoup-1.13.1.jar"),

        DNSJAVA("https://repo1.maven.org/maven2/dnsjava/dnsjava/3.2.2/dnsjava-3.2.2.jar"),
    }


}
