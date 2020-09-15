package me.lovesasuna.bot.service

import org.hibernate.Session

interface Service {
    val session: Session
}