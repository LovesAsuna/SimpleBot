package com.hyosakura.bot.controller.picture.hpicture

import com.hyosakura.bot.data.BotData.objectMapper
import com.hyosakura.bot.util.network.OkHttpUtil
import java.io.IOException
import kotlin.random.Random

/**
 * @author LovesAsuna
 */
class Misc : SinglePictureSource {
    override fun fetchData(): String? {
        return when (Random.nextInt(3)) {
            0 -> mty()
            1 -> acg()
            2 -> toubiec()
            else -> mty()
        }
    }

    private fun mty(): String? {
        val source = "https://api.mtyqx.cn/api/random.php?return=json"
        val result = OkHttpUtil.getIs(source)
        return try {
            val root = objectMapper.readTree(result)
            root["imgurl"].asText()
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun acg(): String = "https://api.nmb.show/1985acg.php"

    private fun toubiec(): String = "https://acg.toubiec.cn/random.php"
}