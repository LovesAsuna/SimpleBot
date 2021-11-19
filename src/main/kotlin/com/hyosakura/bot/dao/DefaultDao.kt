package com.hyosakura.bot.dao

import org.jetbrains.exposed.sql.Database


/**
 * @author LovesAsuna
 **/
interface DefaultDao {
    val database: Database
}