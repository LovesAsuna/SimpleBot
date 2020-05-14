package me.lovesasuna.bot.util

import org.xbill.DNS.Lookup
import org.xbill.DNS.SRVRecord
import org.xbill.DNS.TextParseException
import org.xbill.DNS.Type

object SRVConvertUtil {
    @Throws(TextParseException::class)
    fun convert(host: String): String? {
        var host = host
        var port : Int
        val records = Lookup("_minecraft._tcp.$host", Type.SRV).run()
        return if (records != null && records.size > 0) {
            val result = records[0] as SRVRecord
            host = result.target.toString().replaceFirst("\\.$".toRegex(), "")
            port = result.port
            "$host:$port"
        } else {
            null
        }
    }
}