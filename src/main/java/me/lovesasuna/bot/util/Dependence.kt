package me.lovesasuna.bot.util

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.*
import me.lovesasuna.bot.Agent
import me.lovesasuna.bot.Main
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.data.DependenceData
import me.lovesasuna.bot.util.file.FileUtil
import me.lovesasuna.bot.util.network.DownloadUtil
import me.lovesasuna.bot.util.plugin.Logger
import me.lovesasuna.bot.util.plugin.display.ProgressBarImpl
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import kotlin.system.exitProcess

/**
 * @author LovesAsuna
 * @date 2020/5/2 14:59
 */
class Dependence constructor(private val fileName: String, val urlData: DependenceData.DependenceUrl, MD5Data: DependenceData.MD5) {
    val MD5: String = MD5Data.data
    lateinit var conn: AtomicReference<HttpURLConnection>
    var needDownload = false
    var finish = true
    val fileURL: URL

    companion object {
        private var depenDir: File? = null
        private var totalSize = 1
        private var downloadedSize = 0
        private val progressBar = ProgressBarImpl(50).also { it.setInterval(500) }
        private fun download(dependence: Dependence) {
            GlobalScope.launch {
                download(dependence.conn, getFile(dependence))
                dependence.finish = true
            }
        }

        private fun getFile(dependence: Dependence): File {
            return File("${Main.dataFolder.path}${File.separator}Dependencies${File.separator}${dependence.fileName}")
        }

        private fun getResource(dependence: Dependence) {
            val dependenceFile = getFile(dependence)
            /*文件不存在*/
            if (!dependenceFile.exists()) {
                dependence.needDownload = true
            } else {
                /*文件存在*/
                if (dependence.MD5 != FileUtil.getFileMD5(dependenceFile)) {
                    /*MD5不匹配*/
                    dependence.needDownload = true
                }
            }
            if (dependence.needDownload) {
                dependence.finish = false
                dependence.conn = AtomicReference()
                dependence.conn.set(URL(dependence.urlData.data).openConnection() as HttpURLConnection)
                when (dependence.urlData) {
                    is DependenceData.LanzousUrl -> {
                        dependence.conn.get().setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9")
                    }
                }
                dependence.conn.get().connect()
                totalSize += dependence.conn.get().contentLength
            }
        }

        private fun download(conn: AtomicReference<HttpURLConnection>, file: File) {
            try {
                DownloadUtil.download(conn.get(), file) { i ->
                    downloadedSize += i
                    progressBar.index = downloadedSize.toDouble() / totalSize * progressBar.PROGRESS_SIZE
                }
            } catch (e: IOException) {
                println()
                System.err.println("An error occurred when downloading dependency: ${file.name}")
                System.err.println("URL: ${conn.get().url}")
                System.err.println("Process auto exit...")
                exitProcess(0)
            }
        }

        fun init() {
            Logger.log(Logger.Messages.DOWNLOAD_DEPEN, Logger.LogLevel.CONSOLE)
            GlobalScope.launch {
                val dependencies = ArrayList<Dependence>()
                dependencies.apply {
                    add(Dependence("jackson-databind-2.11.1.jar", DependenceData.Maven.JACKSON_DATABIND, DependenceData.MD5.JACKSON_DATABIND))
                    add(Dependence("jackson-core-2.11.1.jar", DependenceData.Maven.JACKSON_CORE, DependenceData.MD5.JACKSON_CORE))
                    add(Dependence("jackson-annotations-2.11.1.jar", DependenceData.Maven.JACKSON_ANNOTATIONS, DependenceData.MD5.JACKSON_ANNOTATIONS))
                    add(Dependence("jackson-module-kotlin-2.11.1.jar", DependenceData.Maven.JACKSON_MODULE, DependenceData.MD5.JACKSON_MODULE))
                    add(Dependence("custom-core-1.2.1.jar", DependenceData.Lanzous.CUSTOMCORE, DependenceData.MD5.CUSTOMCORE))
                    add(Dependence("jsoup-1.13.1.jar", DependenceData.Maven.JSOUP, DependenceData.MD5.JSOUP))
                    add(Dependence("dnsjava-3.2.2.jar", DependenceData.Maven.DNSJAVA, DependenceData.MD5.DNSJAVA))
                    forEach {
                        getResource(it)
                    }

                    forEach {
                        if (it.needDownload) {
                            download(it)
                        }
                    }
                }

                while (true) {
                    var finish = true
                    for (dependence in dependencies) {
                        if (!dependence.finish) {
                            finish = false
                            break
                        }
                    }

                    if (finish) {
                        break
                    }
                    delay(500)
                }

                for (dependence in dependencies) {
                    Agent.addToClassPath(Paths.get(dependence.fileURL.toURI()))
                }
                BotData.objectMapper = jacksonObjectMapper().also { it.propertyNamingStrategy = PropertyNamingStrategy.LOWER_CASE }
                progressBar.index = 100.0
            }
            progressBar.print()
        }

        init {
            depenDir = File("${Main.dataFolder.path}${File.separator}Dependencies").also {
                if (!it.exists()) {
                    Files.createDirectories(Paths.get(it.toURI()))
                }
            }
        }
    }

    init {
        fileURL = File(depenDir!!.path + File.separator + fileName).toURI().toURL()
    }

}
