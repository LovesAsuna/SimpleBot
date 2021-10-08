package com.hyosakura.bot.service

import java.io.Closeable

object ServiceManager {
    private val serviceList = mutableListOf<Closeable>()

    fun registerService(service : Closeable) = serviceList.add(service)

    fun unRegisterService(service : Closeable) = serviceList.remove(service)

    fun closeAll() : Boolean {
        return kotlin.runCatching {
            for (service in serviceList) {
                service.close()
            }
        }.isSuccess
    }
}