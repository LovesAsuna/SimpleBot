package me.lovesasuna.bot.service

import me.lovesasuna.bot.entity.DynamicEntity
import org.hibernate.SessionFactory

interface DynamicService {
    val factory: SessionFactory

    fun save(entity : DynamicEntity)

    fun update()
}