package com.hyosakura.bot.util

import kotlinx.coroutines.Job
import kotlinx.coroutines.Runnable
import com.hyosakura.bot.Main
import me.lovesasuna.bot.util.network.OkHttpUtil
import me.lovesasuna.bot.util.plugin.PluginScheduler
import net.mamoe.mirai.console.permission.Permission
import net.mamoe.mirai.console.permission.PermissionId
import net.mamoe.mirai.console.permission.PermissionService
import java.io.File
import java.nio.file.Paths
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

/**
 * @author LovesAsuna
 */
object BasicUtil {
    fun extractInt(string: String, defaultValue: Int = 0): Int {
        val pattern = Pattern.compile("\\d+")
        val buffer = StringBuffer()
        val matcher = pattern.matcher(string)
        while (matcher.find()) {
            buffer.append(matcher.group())
        }
        return if (buffer.toString().isEmpty()) defaultValue else buffer.toString().toInt()
    }

    fun <T> replace(message: String, `var`: String, replace: T): String {
        return message.replace(`var`, replace.toString())
    }

    /**
     * @param command 任务
     * @param delay 延迟(单位:秒)
     */
    fun scheduleWithFixedDelay(
        command: Runnable,
        initialDelay: Long,
        delay: Long,
        unit: TimeUnit
    ): Pair<Job, PluginScheduler.RepeatTaskReceipt> =
        com.hyosakura.bot.Main.scheduler.scheduleWithFixedDelay(command, initialDelay, delay, unit)

    fun debug(message: String): String {
        val reader = OkHttpUtil.getIs(
            OkHttpUtil.post(
                "https://paste.ubuntu.com/", mapOf(
                    "poster" to "Bot",
                    "syntax" to "text",
                    "expiration" to "day",
                    "content" to message,
                ), OkHttpUtil.addHeaders(
                    mapOf(
                        "host" to "paste.ubuntu.com",
                        "cookie" to "sessionid=6h8n5um9l2tyk9guhf7b0mvz2qie2uuh"
                    )
                )
            )
        ).bufferedReader()
        val text = reader.lines().skip(52).filter {
            it.contains("p-button--neutral")
        }.findFirst().get()
        val s =
            Regex("<a class=\"p-button--neutral\" href=\"/(p/([0-9a-zA-Z])+)/plain/\">View raw</a>").find(text)!!.groupValues[1]
        return "https://paste.ubuntu.com/$s"
    }

    fun getLocation(c: Class<*>): File {
        return if (System.getProperty("os.name").contains("windows", true)) {
            Paths.get(c.protectionDomain.codeSource.location.path.replaceFirst("/", "")).parent.toFile()
        } else {
            Paths.get(c.protectionDomain.codeSource.location.path).parent.toFile()
        }
    }
}

fun registerDefaultPermission() = registerPermission("default", "默认权限")

fun registerPermission(name: String, description: String): Permission {
    val permissionId = PermissionId("bot", name)
    return PermissionService.INSTANCE[permissionId] ?: PermissionService.INSTANCE.register(permissionId, description)
}
