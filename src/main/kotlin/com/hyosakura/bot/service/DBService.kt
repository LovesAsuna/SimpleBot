package com.hyosakura.bot.service

import org.ktorm.database.Database

interface DBService {
    val database: Database
}