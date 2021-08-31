package com.hyosakura.bot.data

import kotlinx.serialization.Serializable

@Serializable
data class ConfigData(
    var Protocol: String = "ANDROID_PAD",
    var Admin: Array<Long> = arrayOf(0),
    var Account: Long = 0,
    var Password: String = "",
    val API: Map<String, Array<String>> = mapOf(
        "SauceNaoAPI" to arrayOf("")
    ),
    var BilibiliCookie: String = "没什么用的功能",
    var LanzouCookie: String = "此功能暂时禁用",
    var DisableFunction: Array<String> = arrayOf("")
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ConfigData

        if (!Admin.contentEquals(other.Admin)) return false
        if (!DisableFunction.contentEquals(other.DisableFunction)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = Admin.contentHashCode()
        result = 31 * result + DisableFunction.contentHashCode()
        return result
    }
}