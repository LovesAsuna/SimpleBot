package com.hyosakura.bot.dao

import me.lovesasuna.bot.entity.game.Server
import me.lovesasuna.bot.entity.game.TeamSpeakEntity
import org.hibernate.Session

/**
 * @author LovesAsuna
 **/
class TeamSpeakDao(override val session: Session) : DefaultHibernateDao<TeamSpeakEntity>(session) {
    fun getAllServer(): List<TeamSpeakEntity> {
        return queryEntity(
            "from TeamSpeakEntity",
            TeamSpeakEntity::class.java,
        )
    }

    fun queryServer(host: String, port: Int): TeamSpeakEntity? {
        return session.get(TeamSpeakEntity::class.java, Server(host, port))
    }

    fun addServer(host: String, port: Int, username: String, password: String, groups: MutableSet<Long>) {
        session.saveOrUpdate(TeamSpeakEntity(Server(host, port), username, password, groups))
    }

    fun deleteServer(host: String, port: Int): Boolean {
        session.delete(TeamSpeakEntity(Server(host, port)))
        return true
    }
}