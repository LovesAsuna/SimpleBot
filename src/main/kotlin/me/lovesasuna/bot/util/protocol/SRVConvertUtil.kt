package me.lovesasuna.bot.util.protocol

import java.nio.charset.Charset

object SRVConvertUtil {
    private val runtime = Runtime.getRuntime()

    fun convert(host: String): String {
        val process = runtime.exec("cmd /c nslookup -qt=SRV _minecraft._tcp.$host")
        val reader = process.inputStream.bufferedReader(Charset.forName("GBK"))
        (0..5).forEach { _ -> reader.readLine() }
        val port = reader.readLine().split("=")[1].trim()
        val ip = reader.readLine().split("=")[1].trim()
        return "$ip:$port"
    }
}