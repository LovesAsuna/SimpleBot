package me.lovesasuna.bot.controller.game

import com.github.theholywaffle.teamspeak3.TS3Config
import com.github.theholywaffle.teamspeak3.TS3Query
import com.github.theholywaffle.teamspeak3.api.event.*
import me.lovesasuna.bot.Main
import me.lovesasuna.bot.entity.game.Server
import me.lovesasuna.bot.entity.game.TeamSpeakEntity
import me.lovesasuna.bot.service.TeamSpeakService
import me.lovesasuna.bot.service.impl.TeamSpeakImpl
import me.lovesasuna.bot.util.BasicUtil
import me.lovesasuna.bot.util.registerDefaultPermission
import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.command.getGroupOrNull
import java.util.concurrent.TimeUnit

object TeamSpeak : CompositeCommand(
    owner = Main,
    primaryName = "ts",
    description = "teamspeak客户端",
    parentPermission = registerDefaultPermission()
) {
    private val teamSpeakService: TeamSpeakService = TeamSpeakImpl
    val queries = hashMapOf<Server, TS3Query>()

    init {
        teamSpeakService.getAllServer().forEach {
            queries[it.server!!] = initQuery(it)
        }
        BasicUtil.scheduleWithFixedDelay({
            while (queries.isNotEmpty()) {
                queries.forEach { (server, query) ->
                    if (!query.isConnected) {
                        try {
                            query.exit()
                        } catch (e: Exception) {
                        }
                        queries[server] = initQuery(teamSpeakService.queryServer(server.host!!, server.port!!)!!)
                    }
                }
            }
        }, 0, 1, TimeUnit.SECONDS)
    }

    @SubCommand
    suspend fun CommandSender.addServer(host: String, port: Int, username: String, password: String) {
        val entity = teamSpeakService.addServer(host, port, username, password, getGroupOrNull()!!.id)
        queries[Server(host, port)] = initQuery(entity!!)
        sendMessage("ts服务器添加成功")
    }

    @SubCommand
    suspend fun CommandSender.removeServer(host: String, port: Int) {
        teamSpeakService.deleteServer(host, port)
        queries.remove(Server(host, port))?.exit()
        sendMessage("ts服务器删除成功")
    }

    @SubCommand
    suspend fun CommandSender.serverList() {
        val builder = StringBuilder("==================\n")
        teamSpeakService.getServerByGroup(getGroupOrNull()!!.id).forEach {
            builder.append("${it.server?.host}:${it.server?.port}\n")
        }
        builder.append("==================")
        sendMessage(builder.toString())
    }

    private fun initQuery(entity: TeamSpeakEntity): TS3Query {
        val query = TS3Query(TS3Config().run {
            this.setHost(entity.server!!.host!!)
                .setQueryPort(entity.server!!.port!!)
                .setEnableCommunicationsLogging(true)
        })
        query.connect()
        val api = query.api
        api.login(entity.username, entity.password)
        api.selectVirtualServerById(1)
        api.registerAllEvents()
        val sendMessageFunction = { message: String ->
            entity.groups?.forEach {
                val group = Bot.instances[0].getGroup(it)
                if (group != null) {
                    Main.scheduler.asyncTask {
                        group.sendMessage(message)
                    }
                }
            }
        }
        api.addTS3Listeners(object : TS3EventAdapter() {
            private val userMap = hashMapOf<Int, String>()

            override fun onClientJoin(e: ClientJoinEvent) {
                sendMessageFunction.invoke(e.clientNickname + "加入了TeamSpeak服务器")
                userMap[e.clientId] = e.clientNickname
            }

            override fun onClientLeave(e: ClientLeaveEvent) {
                sendMessageFunction.invoke(userMap[e.clientId] + "离开了TeamSpeak服务器")
                userMap.remove(e.clientId)
            }

            override fun onClientMoved(e: ClientMovedEvent) {
                val clientName = api.getClientInfo(e.clientId).nickname
                val channelName = api.getChannelInfo(e.targetChannelId).name
                sendMessageFunction.invoke(clientName + "移动到了频道[$channelName]")
            }

            override fun onChannelCreate(e: ChannelCreateEvent) {
                val channelName = api.getChannelInfo(e.channelId).name
                sendMessageFunction.invoke("频道${channelName}被创建")
            }

            override fun onChannelDeleted(e: ChannelDeletedEvent) {
                val channelName = api.getChannelInfo(e.channelId).name
                sendMessageFunction.invoke("频道${channelName}被删除")
            }
        })
        return query
    }
}