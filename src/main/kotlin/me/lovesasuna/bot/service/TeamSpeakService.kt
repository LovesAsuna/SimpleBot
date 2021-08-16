package me.lovesasuna.bot.service

import me.lovesasuna.bot.entity.TeamSpeakEntity

/**
 * @author LovesAsuna
 **/
interface TeamSpeakService : DBService {
    fun getAllServer(): List<TeamSpeakEntity>

    fun getServerByGroup(groupID: Long): List<TeamSpeakEntity>

    fun queryServer(host: String, port: Int): TeamSpeakEntity?

    fun addServer(host: String, port: Int, username: String, password: String, groupID: Long): TeamSpeakEntity?

    fun deleteServer(host: String, port: Int): Boolean
}