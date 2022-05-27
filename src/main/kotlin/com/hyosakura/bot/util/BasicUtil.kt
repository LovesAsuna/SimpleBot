package com.hyosakura.bot.util

import com.hyosakura.bot.Main
import com.hyosakura.bot.util.coroutine.PluginScheduler
import com.hyosakura.bot.util.network.Request
import com.hyosakura.bot.util.network.Request.getCookie
import io.ktor.client.statement.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.Runnable
import net.mamoe.mirai.console.permission.Permission
import net.mamoe.mirai.console.permission.PermissionId
import net.mamoe.mirai.console.permission.PermissionService
import org.jsoup.Jsoup
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
        Main.scheduler.scheduleWithFixedDelay(command, initialDelay, delay, unit)

    suspend fun <R> withTimeOut(
        consumer: suspend () -> R,
        delayMs: Long,
        notCompletedAction: (suspend (Throwable) -> Unit)? = null
    ): R? = Main.scheduler.withTimeOut(consumer, delayMs, notCompletedAction)

    suspend fun debug(message: String): String {
        Main.logger.debug(message)
        val response = Request.submitForm(
            "https://paste.ubuntu.com/",
            mapOf(
                "poster" to "Bot",
                "syntax" to "text",
                "expiration" to "day",
                "content" to message,
            ),
            10000,
            headers = mapOf(
                "Referer" to "paste.ubuntu.com",
            )
        )
        val location = response.headers["location"]
        return if (location == null) {
            "发生错误！"
        } else {
            "https://paste.ubuntu.com$location"
        }
    }

    fun getLocation(c: Class<*>): File {
        return if (System.getProperty("os.name").contains("windows", true)) {
            Paths.get(c.protectionDomain.codeSource.location.path.replaceFirst("/", "")).parent.toFile()
        } else {
            Paths.get(c.protectionDomain.codeSource.location.path).parent.toFile()
        }
    }

    suspend fun ubuntuLogin(email : String, password : String): String {
        val form = mutableMapOf<String, String>()
        Request.getStr("https://paste.ubuntu.com/openid/login/").run {
            Jsoup.parse(this).select("form input").forEach {
                form[it.attr("name")] = it.attr("value")
            }
        }
        // 第一次请求+openid
        var url = "https://login.ubuntu.com/+openid"
        var response = Request.submitForm(
            url, form, headers = mapOf("Referer" to "https://paste.ubuntu.com/")
        )
        var cookie = response.getCookie()
        var location = response.headers["location"]
        // 第二次请求+decide
        url = "https://login.ubuntu.com$location"
        response = Request.get(url, headers = mapOf("Cookie" to cookie, "Referer" to "https://paste.ubuntu.com/"))
        location = response.headers["location"]
        // 第三次请求cookie?next
        url = "https://login.ubuntu.com$location"
        cookie += "C=1; "
        response = Request.get(url, headers = mapOf("Cookie" to cookie, "Referer" to "https://paste.ubuntu.com/"))
        // 第四次请求+decide
        location = response.headers["location"]
        url = "https://login.ubuntu.com$location"
        val csrfToken: String
        response = Request.get(url, headers = mapOf("Cookie" to cookie, "Referer" to "https://paste.ubuntu.com/"))
        cookie += response.getCookie().also {
            csrfToken = it.substringBefore(";").substringAfter("=")
        }
        // 第五次请求+login
        response = Request.submitForm(
            url.substringBefore('+') + "+login", mapOf(
                "csrfmiddlewaretoken" to csrfToken,
                "email" to email,
                "user-intentions" to "login",
                "password" to password,
                "continue" to "",
                "openid.usernamesecret" to ""
            ), headers = mapOf("Cookie" to cookie, "Referer" to url)
        )
        cookie = response.getCookie() + "C=1"
        location = response.headers["location"]
        // 第六次请求
        response =
            Request.get("https://login.ubuntu.com$location", headers = mapOf("Cookie" to cookie, "Referer" to url))
        // 第七次请求
        location = response.headers["location"]
        response =
            Request.get("https://login.ubuntu.com$location", headers = mapOf("Cookie" to cookie, "Referer" to url))
        // 第八次请求
        val doc = Jsoup.parse(response.readText())
        val action = doc.select("form").attr("action")
        form.clear()
        doc.select("form input").forEach {
            form[it.attr("name")] = it.attr("value")
        }
        response = Request.submitForm(
            action,
            form,
            headers = mapOf("Cookie" to cookie, "Referer" to "https://login.ubuntu.com/")
        )
        return response.getCookie()
    }
}

fun registerDefaultPermission() = registerPermission("default", "默认权限")

fun registerPermission(name: String, description: String): Permission {
    val permissionId = PermissionId("bot", name)
    return PermissionService.INSTANCE[permissionId] ?: PermissionService.INSTANCE.register(permissionId, description)
}
