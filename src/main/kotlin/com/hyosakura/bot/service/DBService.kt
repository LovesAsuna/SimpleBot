package com.hyosakura.bot.service

import org.hibernate.Session

interface DBService {
    val session: Session

    fun close() {
        session.close()
    }
}