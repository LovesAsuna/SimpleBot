package com.hyosakura.bot.service

import org.hibernate.Session
import java.io.Closeable

interface DBService : Closeable {
    val session: Session

    override fun close() {
        session.close()
    }
}