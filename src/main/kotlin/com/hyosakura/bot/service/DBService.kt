package com.hyosakura.bot.service

import org.jetbrains.exposed.sql.Database


interface DBService {
    val database: Database
}