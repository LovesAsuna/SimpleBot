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

    var debug: Boolean = false

    val HibernateConfig: Configuration = Configuration().also {
        ClassUtil.getClasses("me.lovesasuna.bot.entity", Main::class.java.classLoader).forEach { c ->
            it.addAnnotatedClass(c)
        }
        Thread.currentThread().contextClassLoader = this.javaClass.classLoader
        it.configure()
    }
}