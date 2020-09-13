package me.lovesasuna.bot.service

import org.hibernate.SessionFactory

interface Service {
    val factory: SessionFactory
}