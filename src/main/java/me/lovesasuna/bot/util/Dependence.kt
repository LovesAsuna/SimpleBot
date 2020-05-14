package me.lovesasuna.bot.util

import com.fasterxml.jackson.databind.ObjectMapper
import me.lovesasuna.bot.Main
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.data.DependenceData
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
class Dependence constructor(private val fileName: String, url: DependenceData, MD5: DependenceData) {
    val MD5: String
    val url: String
    var finish = false
    val fileURL: URL

    companion object {
        private var downloadDepen = false
        private var pool: ThreadPoolExecutor? = null
        private var depenDir: File? = null
        fun download(dependence: Dependence) {
            pool!!.submit {
                if (!depenDir!!.exists()) {
                    try {
                        Files.createDirectory(Paths.get(depenDir!!.path))
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                var url: URL?
                val conn = AtomicReference<HttpURLConnection>()
                try {
                    url = URL(dependence.url)
                    conn.set(url.openConnection() as HttpURLConnection)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                val dependenceFile = File(Main.instance.dataFolder.path + File.separator + "Dependencies" + File.separator + dependence.fileName)

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
                DownloadUtil.download(conn.get(), file)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        fun init() {
            if (downloadDepen) {
                return
            }
            downloadDepen = true
            BotData.scheduledpool.execute {
                val dependences: MutableList<Dependence> = ArrayList()
                dependences.add(Dependence("jackson-databind-2.10.3.jar", DependenceData.JACKSON_DATABIND_URL, DependenceData.JACKSON_DATABIND_MD5))
                dependences.add(Dependence("jackson-core-2.10.3.jar", DependenceData.JACKSON_CORE_URL, DependenceData.JACKSON_CORE_MD5))
                dependences.add(Dependence("jackson-annotations-2.10.3.jar", DependenceData.JACKSON_ANNOTATIONS_URL, DependenceData.JACKSON_ANNOTATIONS_MD5))
                dependences.add(Dependence("ZXING-Core-3.4.0.jar", DependenceData.ZXING_URL, DependenceData.ZXING_MD5))

                for (dependence in dependences) {
                    download(dependence)
                }

                while (true) {
                    var finish = true
                    for (dependence in dependences) {
                        if (!dependence.finish) {
                            finish = false
                        }
                    }
                    if (finish) {
                        break
                    }
                }
                val addURL = Class.forName("java.net.URLClassLoader").getDeclaredMethod("addURL", URL::class.java)
                addURL.isAccessible = true
                for (dependence in dependences) {
                    addURL.invoke(Main::class.java.classLoader, dependence.fileURL)
                }

                Main.mapper = ObjectMapper()
                Main.initListener()
            }

        }

        init {
            pool = ThreadPoolExecutor(5, 5, 1, TimeUnit.MINUTES, ArrayBlockingQueue(5))
            depenDir = File(Main.instance.dataFolder.path + File.separator + "Dependencies")
        }
    }

    init {
        this.MD5 = MD5.data
        this.url = url.data
        fileURL = File(depenDir!!.path + File.separator + fileName).toURI().toURL()
    }

}
