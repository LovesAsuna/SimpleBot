package me.lovesasuna.bot.data

import com.fasterxml.jackson.databind.ObjectMapper
import java.util.concurrent.ScheduledThreadPoolExecutor


/**
 * @author LovesAsuna
 * @date 2020/2/15 20:11
 */
object BotData {
    /*插件专用计划线程池*/
    val scheduledpool: ScheduledThreadPoolExecutor = ScheduledThreadPoolExecutor(1)

    var objectMapper: ObjectMapper? = null

}
