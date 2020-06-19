package me.lovesasuna.bot.util

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import me.lovesasuna.bot.Main
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

/**
 * @author LovesAsuna
 * @date 2020/4/19 14:05
 */
object BasicUtil {
    fun ExtraceInt(string: String?): Int {
        val pattern = Pattern.compile("\\d+")
        val buffer = StringBuffer()
        val matcher = pattern.matcher(string)
        while (matcher.find()) {
            buffer.append(matcher.group())
        }
        return if (buffer.toString().isEmpty()) 0 else buffer.toString().toInt()
    }

    fun <T> replace(message: String, `var`: String, replace: T): String {
        return message.replace(`var`, replace.toString())
    }

    fun getFileMD5(file: File): String? {
        return try {
            val bytes = ByteArray(8192)
            var len: Int
            val inputStream = FileInputStream(file)
            val messageDigest = MessageDigest.getInstance("MD5")
            while (inputStream.read(bytes).also { len = it } != -1) {
                messageDigest.update(bytes, 0, len)
            }
            inputStream.close()
            val md5bytes = messageDigest.digest()
            val bigInteger = BigInteger(1, md5bytes)
            bigInteger.toString(16)
        } catch (e: IOException) {
            null
        } catch (e: NoSuchAlgorithmException) {
            null
        }
    }

    /**
     * @param command 任务
     * @param delay 延迟(单位:秒)
     */
    fun scheduleWithFixedDelay(command: Runnable, initialDelay: Long, delay: Long, unit: TimeUnit) {
        runBlocking {
            delay(unit.toMillis(initialDelay))
            Main.instance.scheduler!!.repeat(command, unit.toMillis(delay))
        }
    }

}