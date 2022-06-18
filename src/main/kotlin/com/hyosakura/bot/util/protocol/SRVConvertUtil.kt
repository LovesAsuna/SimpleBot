package com.hyosakura.bot.util.protocol

object SRVConvertUtil {
    private val runtime = Runtime.getRuntime()

    fun convert(host: String): String? {
        val process = runtime.exec(arrayOf("nslookup", "-qt=SRV", "_minecraft._tcp.$host"))
        val reader = process.inputStream.bufferedReader()
        repeat(6) {
            reader.readLine()
        }
        val port = reader.readLine()?.split("=")?.get(1)?.trim() ?: return null
        val ip = reader.readLine().split("=")[1].trim()
        return "$ip:$port"
    }
}