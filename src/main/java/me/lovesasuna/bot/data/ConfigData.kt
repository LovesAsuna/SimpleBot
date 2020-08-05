package me.lovesasuna.bot.data

data class ConfigData(var protocol: String = "ANDROID_PAD",
                      var account: Long = 0,
                      var admin: Long = 0,
                      var password: String = "",
                      var pictureSearchAPI: String = "",
                      var bilibiliCookie: String = "",
                      var lanzouCookie: String = "")