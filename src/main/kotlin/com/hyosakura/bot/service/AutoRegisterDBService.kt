package com.hyosakura.bot.service

/**
 * @author LovesAsuna
 **/
@Suppress("LeakingThisInConstructor")
abstract class AutoRegisterDBService() : DBService {
    init {
        @Suppress("LeakingThis")
        ServiceManager.registerService(this)
    }
}