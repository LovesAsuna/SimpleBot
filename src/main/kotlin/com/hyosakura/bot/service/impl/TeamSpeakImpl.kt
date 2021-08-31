package com.hyosakura.bot.service.impl

import me.lovesasuna.bot.dao.TeamSpeakDao
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.entity.game.Server
import me.lovesasuna.bot.entity.game.TeamSpeakEntity
import me.lovesasuna.bot.service.TeamSpeakService
import org.hibernate.Session
import java.util.stream.Collectors

object TeamSpeakImpl : TeamSpeakService {

    override val session: Session = BotData.functionConfig.buildSessionFactory().openSession()

    private val dao: TeamSpeakDao by lazy { TeamSpeakDao(session) }

    override fun getAllServer(): List<TeamSpeakEntity> = dao.getAllServer()

    override fun getServerByGroup(groupID: Long): List<TeamSpeakEntity> {
        return getAllServer().parallelStream().filter {
            it.groups?.contains(groupID) ?: false
        }.collect(Collectors.toList())
    }

    override fun queryServer(host: String, port: Int) = dao.queryServer(host, port)

    override fun addServer(
        host: String,
        port: Int,
        username: String,
        password: String,
        groupID: Long
    ): TeamSpeakEntity {
        if (!session.transaction.isActive) {
            session.beginTransaction()
        }
        try {
            var server = queryServer(host, port)
            if (server != null) {
                server.groups!!.add(groupID)
            } else {
                server = TeamSpeakEntity(Server(host, port), username, password, mutableSetOf(groupID))
            }
            session.saveOrUpdate(server)
            return server
        } finally {
            session.transaction.commit()
        }
    }

    override fun deleteServer(host: String, port: Int): Boolean {
        if (!session.transaction.isActive) {
            session.beginTransaction()
        }
        try {
            return dao.deleteServer(host, port)
        } finally {
            session.transaction.commit()
        }
    }

}