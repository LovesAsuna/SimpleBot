package me.lovesasuna.bot.controller.Danmu

import com.fasterxml.jackson.databind.JsonNode

class DanmuData internal constructor(jsonNode: JsonNode, private val roomID: Int, version: Int) {
    var type: String? = null
    private var userName: String? = null
    private var commentText: String? = null
    override fun toString(): String {
        return "[Room:$roomID]$userName: $commentText"
    }

    companion object {
        const val COMMENT_TYPE = "Comment"
        const val LIVE_START_TYPE = "LiveStart"
        const val LIVE_STOP_TYPE = "LiveStop"
        private const val UNKNOWN_TYPE = "Unknown"
    }

    init {
        when (version) {
            1 -> {
                commentText = jsonNode[1].asText()
                userName = jsonNode[2][1].asText()
                type = COMMENT_TYPE
            }
            2 -> {
                val cmd = jsonNode["cmd"].asText()
                when (cmd) {
                    "LIVE" -> {
                        type = LIVE_START_TYPE
                    }
                    "PREPARING" -> {
                        type = LIVE_STOP_TYPE
                    }
                    "DANMU_MSG" -> {
                        type = COMMENT_TYPE
                        val info = jsonNode["info"]
                        commentText = info[1].asText()
                        userName = info[2][1].asText()
                    }
                    "SEND_GIFT" -> {
                    }
                    else -> {
                        if (cmd.startsWith("DANMU_MSG")) {
                            type = COMMENT_TYPE
                            val info = jsonNode["info"]
                            commentText = info[1].asText()
                            userName = info[2][1].asText()
                        } else {
                            type = UNKNOWN_TYPE
                        }
                    }
                }
            }
            else -> {
                throw IllegalArgumentException("Version is unknown:$version")
            }
        }
    }
}
