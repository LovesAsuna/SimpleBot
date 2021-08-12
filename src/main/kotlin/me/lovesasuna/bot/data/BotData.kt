package me.lovesasuna.bot.data

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import me.lovesasuna.bot.Main
import me.lovesasuna.bot.util.ClassUtil
import org.hibernate.cfg.Configuration

/**
 * @author LovesAsuna
 */
object BotData {
    var objectMapper: ObjectMapper =
        jacksonObjectMapper().also { it.propertyNamingStrategy = PropertyNamingStrategies.LOWER_CASE }

    val configBlock: (Configuration, String) -> Unit = { config, string ->
        ClassUtil.getClasses("me.lovesasuna.bot.entity", Main::class.java.classLoader).forEach { c ->
            config.addAnnotatedClass(c)
        }
        val classLoader = Thread.currentThread().contextClassLoader
        Thread.currentThread().contextClassLoader = this.javaClass.classLoader
        config.configure(string)
        Thread.currentThread().contextClassLoader = classLoader
    }

    val functionConfig: Configuration = Configuration().also {
      configBlock.invoke(it, "function.xml")
    }

    val recordConfig: Configuration = Configuration().also {
        configBlock.invoke(it, "record.xml")
    }
}