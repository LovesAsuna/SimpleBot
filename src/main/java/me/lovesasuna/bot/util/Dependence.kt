package me.lovesasuna.bot.util

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.lovesasuna.bot.Agent
import me.lovesasuna.bot.Main
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.data.DependenceData
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
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

/**
 * @author LovesAsuna
 * @date 2020/5/2 14:59
 */
class Dependence constructor(private val fileName: String, url: DependenceData.URL, MD5: DependenceData.MD5) {
    val MD5: String = MD5.data
    val url: String = url.data
    var finish = false
    val fileURL: URL

    companion object {
        private var pool: ThreadPoolExecutor? = null
        private var depenDir: File? = null
        private var totalSize = 1
        private var downloadedSize = 0
        private val progressBar = ProgressBarImpl(50).also { it.setInterval(500) }
        private fun download(dependence: Dependence) {
            pool!!.submit {
                val url: URL?
                val conn = AtomicReference<HttpURLConnection>()
                try {
                    url = URL(dependence.url)
                    conn.set(url.openConnection() as HttpURLConnection)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                val dependenceFile = File(Main.dataFolder.path + File.separator + "Dependencies" + File.separator + dependence.fileName)

                /*文件不存在*/
                if (!dependenceFile.exists()) {
                    download(conn, dependenceFile)
                    dependence.finish = true
                } else {
                    /*文件存在*/
                    if (dependence.MD5 != BasicUtil.getFileMD5(dependenceFile)) {
                        /*MD5不匹配*/
                        download(conn, dependenceFile)
                    }
                    dependence.finish = true
                }
            }
        }


        private fun download(conn: AtomicReference<HttpURLConnection>, file: File) {
            try {
                conn.get().connect()
                totalSize += conn.get().contentLength
                DownloadUtil.download(conn.get(), file) { i ->
                    downloadedSize += i
                    progressBar.index = (progressBar.PROGRESS_SIZE * downloadedSize).toDouble() / totalSize
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        fun init() {
            Logger.log(Logger.Messages.DOWNLOAD_DEPEN, Logger.LogLevel.CONSOLE)
            GlobalScope.launch {
                val dependencies = ArrayList<Dependence>()
                dependencies.apply {
                    add(Dependence("jackson-databind-2.11.1.jar", DependenceData.URL.JACKSON_DATABIND, DependenceData.MD5.JACKSON_DATABIND))
                    add(Dependence("jackson-core-2.11.1.jar", DependenceData.URL.JACKSON_CORE, DependenceData.MD5.JACKSON_CORE))
                    add(Dependence("jackson-annotations-2.11.1.jar", DependenceData.URL.JACKSON_ANNOTATIONS, DependenceData.MD5.JACKSON_ANNOTATIONS))
                    add(Dependence("jackson-module-kotlin-2.11.1.jar", DependenceData.URL.JACKSON_MODULE, DependenceData.MD5.JACKSON_MODULE))
                    add(Dependence("custom-core-1.1.1.jar", DependenceData.URL.CUSTOMCORE, DependenceData.MD5.CUSTOMCORE))
                }.forEach {
                    download(it)
                }

                while (true) {
                    var finish = true
                    dependencies.forEach {
                        if (!it.finish) {
                            finish = false
                        }
                    }
                    if (finish) {
                        break
                    }
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
            pool = ThreadPoolExecutor(5, 10, 1, TimeUnit.MINUTES, ArrayBlockingQueue(5))
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
