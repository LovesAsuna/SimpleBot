package com.hyosakura.bot.data

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.ktorm.database.Database

/**
 * @author LovesAsuna
 */
object BotData {
    var objectMapper: ObjectMapper =
        jacksonObjectMapper().also { it.propertyNamingStrategy = PropertyNamingStrategies.LOWER_CASE }

    val botDataSource = HikariDataSource(HikariConfig().apply {
        jdbcUrl = "jdbc:h2:./db/bot"
        driverClassName = "org.h2.Driver"
    })
    val messageDataSource = HikariDataSource(HikariConfig().apply {
        jdbcUrl = "jdbc:h2:./db/message"
        driverClassName = "org.h2.Driver"
    })

    val botDatabase = Database.connect(botDataSource)
    val messageDatabase = Database.connect(messageDataSource)
}