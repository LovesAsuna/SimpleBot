package com.hyosakura.bot.data

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.hyosakura.bot.Main
import me.lovesasuna.bot.util.ClassUtil
import org.hibernate.cfg.Configuration

/**
 * @author LovesAsuna
 */
object BotData {
    var objectMapper: ObjectMapper =
        jacksonObjectMapper().also { it.propertyNamingStrategy = PropertyNamingStrategies.LOWER_CASE }

    private val configBlock: (config: Configuration, configFile: String, packages: Array<String>) -> Unit =
        { config, string, packages ->
            packages.forEach {
                ClassUtil.getClasses(it, com.hyosakura.bot.Main::class.java.classLoader).forEach { c ->
                    config.addAnnotatedClass(c)
                }
            }
            val classLoader = Thread.currentThread().contextClassLoader
            Thread.currentThread().contextClassLoader = this.javaClass.classLoader
            config.configure(string)
            Thread.currentThread().contextClassLoader = classLoader
        }

    val functionConfig: Configuration = Configuration().also {
        configBlock.invoke(
            it, "function.xml", arrayOf(
                "me.lovesasuna.bot.entity.dynamic",
                "me.lovesasuna.bot.entity.fun",
                "me.lovesasuna.bot.entity.game"
            )
        )
    }

    val recordConfig: Configuration = Configuration().also {
        configBlock.invoke(
            it, "record.xml", arrayOf(
                "me.lovesasuna.bot.entity.message"
            )
        )
    }
}