package me.lovesasuna.bot.service

import org.hibernate.Session

interface DBService {
    val session: Session
}