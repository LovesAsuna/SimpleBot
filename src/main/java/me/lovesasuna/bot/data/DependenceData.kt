package me.lovesasuna.bot.data


interface DependenceData {
    enum class MD5(val data: String) : DependenceData {
        /*JackSon依赖*/
        JACKSON_ANNOTATIONS("ae29476ef47b802516535a8fbd964ec2"),
        JACKSON_CORE("178171dcc707f3aa1b5d53717530a589"),
        JACKSON_DATABIND("5dc6f0ba9a79ffb5440b9561a55806ed"),
        JACKSON_MODULE("f14829f533c5e5459126f254f009d694"),

        /*Kotlin依赖*/
        KOTLIN_STDLIB("7dea2e32e4b71f48d8863dbef721b408"),
        KOTLIN_STDLIB_ANNOTATIONS("4990efa6b740f88e0772f3b8b815ba03"),
        KOTLIN_STDLIB_COMMON("9c36600bc1179cd6b79a9eb51fefb238"),
        KOTLIN_STDLIB_JDK7("ec6da201a772809331172ed63ec0f3c0"),
        KOTLIN_STDLIB_JDK8("60ea8ff676c976e622f9ae14eac1751"),
        KOTLIN_REFLECT("bce6fbfa240eb95ad6a2e215ed7e6f31"),
        KOTLIN_SERIALIZATION("4ad926d7024253a6b2347b11b256828c"),

        /*Mirai依赖*/
        CUSTOMCORE("ef85e90b643ce3380562a227b2bbd93"),
    }

    enum class URL(val data: String) : DependenceData {
        JACKSON_DATABIND("https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-databind/2.11.1/jackson-databind-2.11.1.jar"),
        JACKSON_MODULE("https://repo1.maven.org/maven2/com/fasterxml/jackson/module/jackson-module-kotlin/2.11.1/jackson-module-kotlin-2.11.1.jar"),
        JACKSON_CORE("https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-core/2.11.1/jackson-core-2.11.1.jar"),
        JACKSON_ANNOTATIONS("https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-annotations/2.11.1/jackson-annotations-2.11.1.jar"),

        CUSTOMCORE("https://dev46.baidupan.com/071313bb/2020/07/13/517483107ef97cf8caa22ead4a332591.jar?st=wJ0jK848tqSf_bZInnWc3Q&e=1594619572&b=BhcNeAFyAnMEPgM4BBcAawIlXmdTfwo9An8APFV8UGMAfwhpVTkEbVkiUm5RYAdx&fi=26192564&pid=113-77-87-252&up=1."),

        KOTLIN_STDLIB("https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-stdlib/1.3.72/kotlin-stdlib-1.3.72.jar"),
        KOTLIN_STDLIB_ANNOTATIONS("https://repo1.maven.org/maven2/org/jetbrains/annotations/19.0.0/annotations-19.0.0.jar"),
        KOTLIN_STDLIB_COMMON("https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-stdlib-common/1.3.72/kotlin-stdlib-common-1.3.72.jar"),
        KOTLIN_STDLIB_JDK7("https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-stdlib-jdk7/1.3.72/kotlin-stdlib-jdk7-1.3.72.jar"),
        KOTLIN_STDLIB_JDK8("https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-stdlib-jdk8/1.3.72/kotlin-stdlib-jdk8-1.3.72.jar"),
        KOTLIN_REFLECT("https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-reflect/1.3.72/kotlin-reflect-1.3.72.jar"),
        KOTLIN_SERIALIZATION("https://repo1.maven.org/maven2/org/jetbrains/kotlinx/kotlinx-serialization-runtime/0.20.0/kotlinx-serialization-runtime-0.20.0.jar");
    }


}
