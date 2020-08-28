package me.lovesasuna.bot.data

import com.fasterxml.jackson.databind.ObjectMapper
import java.util.*

/**
 * @author LovesAsuna
 * @date 2020/2/15 20:11
 */
object BotData {
    var objectMapper: ObjectMapper? = null

    var debug: Boolean = false

    val error = Stack<Throwable>()
}

fun Throwable.pushError() {
    BotData.error.push(this)
}