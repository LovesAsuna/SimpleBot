package me.lovesasuna.bot.entity

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import me.lovesasuna.bot.entity.dynamic.DynamicEntity
import me.lovesasuna.bot.entity.dynamic.LinkEntity
import me.lovesasuna.bot.util.annotations.processors.FilterProcessorHandler
import org.hibernate.cfg.Configuration
import java.util.*

/**
 * @author LovesAsuna
 * @date 2020/2/15 20:11
 */
object BotData {
    var objectMapper: ObjectMapper = jacksonObjectMapper().also { it.propertyNamingStrategy = PropertyNamingStrategy.LOWER_CASE }

    var debug: Boolean = false

    val error = Stack<Throwable>()

    val HibernateConfig: Configuration = Configuration().also {
        FilterProcessorHandler.getClasses("me.lovesasuna.bot.entity").forEach { c ->
            it.addAnnotatedClass(c)
        }
        it.configure()
    }
}

fun Throwable.pushError() {
    BotData.error.push(this)
}