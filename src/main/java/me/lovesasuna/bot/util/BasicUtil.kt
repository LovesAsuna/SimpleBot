package me.lovesasuna.bot.util

import kotlinx.coroutines.*
import me.lovesasuna.bot.Main
import me.lovesasuna.bot.util.network.NetWorkUtil
import me.lovesasuna.bot.util.plugin.PluginScheduler
import java.io.*
import java.math.BigInteger
import java.nio.file.Paths
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream


/**
 * @author LovesAsuna
 * @date 2020/4/19 14:05
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
            BigInteger(1, md5bytes).toString(16)
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
    fun scheduleWithFixedDelay(command: Runnable, initialDelay: Long, delay: Long, unit: TimeUnit): Pair<Job, PluginScheduler.RepeatTaskReceipt> {
        val receipt = PluginScheduler.RepeatTaskReceipt()
        val job = GlobalScope.launch {
            delay(unit.toMillis(initialDelay))
            while (!receipt.cancelled && this.isActive) {
                withContext(Dispatchers.IO) {
                    command.run()
                }
                delay(unit.toMillis(delay))
            }
        }
        return Pair(job, receipt)
    }

    fun debug(message: String): String {
        val reader = NetWorkUtil.post("https://paste.ubuntu.com/",
                "poster=Bot&syntax=text&expiration=day&content=$message".toByteArray(Charsets.UTF_8),
                arrayOf("Content-Type", "application/x-www-form-urlencoded"),
                arrayOf("host", "paste.ubuntu.com")
        )!!.first.bufferedReader()
        val builder = StringBuilder()
        reader.lines().skip(25).parallel().forEach {
            builder.append(it)
        }
        val s = Regex("<a class=\"pturl\" href=\"/p/([0-9]|[a-z]|[A-Z])+/plain/\">Download as text</a>").find(builder.toString())!!.value
        return "https://paste.ubuntu.com" + s.substringAfter("href=\"").substringBefore("/plain")
    }

    fun getLocation(c: Class<*>): File {
        return Paths.get(c.protectionDomain.codeSource.location.path.replaceFirst("/", "")).parent.toFile()
    }

    fun getLocation(fileName: String): File {
        return File("${getLocation(Main::class.java).path}${File.separator}$fileName")
    }

    fun toZip(srcDir: String, destFile: String) {
        var zos: ZipOutputStream? = null
        try {
            zos = ZipOutputStream(FileOutputStream(destFile))
            val sourceFile = File(srcDir)
            compress(sourceFile, zos, sourceFile.name)
        } catch (e: Exception) {
            throw RuntimeException("zip error", e)
        } finally {
            if (zos != null) {
                try {
                    zos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * 递归压缩方法
     *
     * @param sourceFile 源文件
     * @param zos zip输出流
     * @param name 压缩后的名称
     * @throws Exception
     */
    private fun compress(sourceFile: File, zos: ZipOutputStream, name: String) {
        val buf = ByteArray(2048)
        if (sourceFile.isFile) {
            zos.putNextEntry(ZipEntry(name))
            var len: Int
            val `in` = FileInputStream(sourceFile)
            while (`in`.read(buf).also { len = it } != -1) {
                zos.write(buf, 0, len)
            }
            zos.closeEntry()
            `in`.close()
        } else {
            val listFiles = sourceFile.listFiles()
            if (listFiles == null || listFiles.isEmpty()) {
                zos.putNextEntry(ZipEntry("$name/"))
                zos.closeEntry()
            } else {
                for (file in listFiles) {
                    compress(file, zos, name + "/" + file.name)
                }
            }
        }
    }

}