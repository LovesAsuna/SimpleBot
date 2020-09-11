package me.lovesasuna.bot.data

import com.fasterxml.jackson.databind.ObjectMapper
import org.hibernate.cfg.Configuration
import java.util.*

/**
 * @author LovesAsuna
 * @date 2020/2/15 20:11
 */
object BotData {
    var objectMapper: ObjectMapper? = null

    var debug: Boolean = false

    val error = Stack<Throwable>()

    val HibernateConfig = Configuration().configure()
}

fun Throwable.pushError() {
    BotData.error.push(this)
}