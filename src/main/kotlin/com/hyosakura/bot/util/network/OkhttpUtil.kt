package com.hyosakura.bot.util.network

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient.Builder
import okhttp3.RequestBody.Companion.toRequestBody
import okio.ByteString
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit


object OkHttpUtil {
    private val MEDIA_JSON: MediaType = "application/json;charset=utf-8".toMediaType()
    private val MEDIA_STREAM: MediaType = "application/octet-stream".toMediaType()
    private val MEDIA_X_JSON: MediaType = "text/x-json".toMediaType()
    private const val TIME_OUT = 10L
    val mapper = jacksonObjectMapper().also { it.propertyNamingStrategy = PropertyNamingStrategies.LOWER_CASE }
    private var okHttpClient: OkHttpClient = Builder()
        .followRedirects(true)
        .followSslRedirects(false)
        .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
        .readTimeout(TIME_OUT, TimeUnit.SECONDS)
        .build()

    private fun emptyHeaders(): Headers {
        return Headers.Builder().build()
    }

    @Throws(IOException::class)
    operator fun get(url: String, headers: Headers): Response {
        val request: Request = Request.Builder().url(url).headers(headers).build()
        return okHttpClient.newCall(request).execute()
    }

    @Throws(IOException::class)
    operator fun get(url: String, map: Map<String, String>): Response {
        return get(url, addHeaders(map))
    }

    @Throws(IOException::class)
    operator fun get(url: String): Response {
        return get(url, emptyHeaders())
    }

    @Throws(IOException::class)
    fun post(url: String, requestBody: RequestBody, headers: Headers): Response {
        val request: Request = Request.Builder().url(url).post(requestBody).headers(headers).build()
        return okHttpClient.newCall(request).execute()
    }

    @Throws(IOException::class)
    fun post(url: String, requestBody: RequestBody): Response {
        return post(url, requestBody, emptyHeaders())
    }

    @Throws(IOException::class)
    fun post(url: String, map: Map<String, String>, headers: Headers): Response {
        return post(url, mapToFormBody(map), headers)
    }

    @Throws(IOException::class)
    fun post(url: String, map: Map<String, String>): Response {
        return post(url, map, emptyHeaders())
    }

    @Throws(IOException::class)
    fun post(url: String): Response {
        return post(url, HashMap(), emptyHeaders())
    }

    @Throws(IOException::class)
    private fun put(url: String, requestBody: RequestBody, headers: Headers): Response {
        val request: Request = Request.Builder().url(url).put(requestBody).headers(headers).build()
        return okHttpClient.newCall(request).execute()
    }

    @Throws(IOException::class)
    private fun put(url: String, requestBody: RequestBody): Response {
        return put(url, requestBody, emptyHeaders())
    }

    @Throws(IOException::class)
    private fun delete(url: String, requestBody: RequestBody, headers: Headers): Response {
        val request: Request = Request.Builder().url(url).delete(requestBody).headers(headers).build()
        return okHttpClient.newCall(request).execute()
    }

    @Throws(IOException::class)
    private fun delete(url: String, requestBody: RequestBody): Response {
        return delete(url, requestBody, emptyHeaders())
    }

    @Throws(IOException::class)
    fun delete(url: String, map: Map<String, String>, headers: Headers): Response {
        return delete(url, mapToFormBody(map), headers)
    }

    @Throws(IOException::class)
    fun delete(url: String, map: Map<String, String>): Response {
        return delete(url, map, emptyHeaders())
    }

    @Throws(IOException::class)
    fun getStr(response: Response): String {
        return response.body!!.string()
    }

    @Throws(IOException::class)
    fun getJson(response: Response): JsonNode {
        val str: String = getStr(response)
        return mapper.readTree(str)
    }

    @Throws(IOException::class)
    fun getBytes(url: String): ByteArray {
        return getBytes(url, emptyHeaders())
    }

    @Throws(IOException::class)
    fun getBytes(url: String, headers: Headers): ByteArray {
        val response: Response = get(url, headers)
        return getBytes(response)
    }

    @Throws(IOException::class)
    fun getBytes(response: Response): ByteArray {
        return response.body!!.bytes()
    }

    @Throws(IOException::class)
    private fun getByteStr(response: Response): ByteString {
        return response.body!!.byteString()
    }

    @Throws(IOException::class)
    fun getByteStr(url: String, headers: Headers): ByteString {
        val response: Response = get(url, headers)
        return getByteStr(response)
    }

    @Throws(IOException::class)
    fun getByteStr(url: String): ByteString {
        return getByteStr(url, emptyHeaders())
    }

    fun getIs(response: Response): InputStream {
        return response.body!!.byteStream()
    }

    @Throws(IOException::class)
    fun getStr(url: String, headers: Headers): String {
        val response: Response = get(url, headers)
        return getStr(response)
    }

    @Throws(IOException::class)
    fun getStr(url: String): String {
        val response: Response = get(url, emptyHeaders())
        return getStr(response)
    }

    @Throws(IOException::class)
    fun getJson(url: String, headers: Headers): JsonNode {
        val response: Response = get(url, headers)
        return getJson(response)
    }

    @Throws(IOException::class)
    fun getJson(url: String, map: Map<String, String>): JsonNode {
        return getJson(url, addHeaders(map))
    }

    @Throws(IOException::class)
    fun getJson(url: String): JsonNode {
        val response: Response = get(url, emptyHeaders())
        return getJson(response)
    }

    @Throws(IOException::class)
    fun postStr(url: String, requestBody: RequestBody, headers: Headers): String {
        val response: Response = post(url, requestBody, headers)
        return getStr(response)
    }

    @Throws(IOException::class)
    fun postStr(url: String, requestBody: RequestBody): String {
        val response: Response = post(url, requestBody, emptyHeaders())
        return getStr(response)
    }

    @Throws(IOException::class)
    fun postJson(url: String, requestBody: RequestBody, headers: Headers): JsonNode {
        val response: Response = post(url, requestBody, headers)
        return getJson(response)
    }

    @Throws(IOException::class)
    fun postJson(url: String, requestBody: RequestBody): JsonNode {
        val response: Response = post(url, requestBody, emptyHeaders())
        return getJson(response)
    }

    @Throws(IOException::class)
    fun postJson(url: String, map: Map<String, String>, headers: Headers): JsonNode {
        val str = postStr(url, map, headers)
        return mapper.readTree(str)
    }

    @Throws(IOException::class)
    fun postJson(url: String, map: Map<String, String>): JsonNode {
        val str = postStr(url, map, emptyHeaders())
        return mapper.readTree(str)
    }

    private fun mapToFormBody(map: Map<String, String>): RequestBody {
        val builder: FormBody.Builder = FormBody.Builder()
        for ((key, value) in map) {
            builder.add(key, value)
        }
        return builder.build()
    }

    @JvmOverloads
    @Throws(IOException::class)
    fun postStr(url: String, map: Map<String, String>, headers: Headers = emptyHeaders()): String {
        val response: Response = post(url, mapToFormBody(map), headers)
        return getStr(response)
    }

    @JvmOverloads
    @Throws(IOException::class)
    fun deleteStr(url: String, map: Map<String, String>, headers: Headers = emptyHeaders()): String {
        val response: Response = delete(url, mapToFormBody(map), headers)
        return getStr(response)
    }

    @Throws(IOException::class)
    fun deleteJson(url: String, map: Map<String, String>, headers: Headers): JsonNode {
        val str = deleteStr(url, map, headers)
        return mapper.readTree(str)
    }

    @Throws(IOException::class)
    fun deleteJson(url: String, map: Map<String, String>): JsonNode {
        val str = deleteStr(url, map, emptyHeaders())
        return mapper.readTree(str)
    }

    fun addJson(jsonStr: String): RequestBody {
        return jsonStr.toRequestBody(MEDIA_JSON)
    }

    fun addSingleHeader(name: String, value: String): Headers {
        return Headers.Builder().add(name, value).build()
    }

    fun addHeaders(cookie: String = "", referer: String = "", userAgent: String = UA.PC.value): Headers {
        return Headers.Builder().add("cookie", cookie).add("referer", referer).add("user-agent", userAgent).build()
    }

    fun addHeaders(cookie: String, referer: String, ua: UA): Headers {
        return addHeaders(cookie, referer, ua)
    }

    fun addHeaders(cookie: String, referer: String): Headers {
        return addHeaders(cookie, referer, UA.PC.value)
    }

    fun addHeaders(map: Map<String, String>): Headers {
        val builder = Headers.Builder()
        for ((key, value) in map) {
            builder.add(key, value)
        }
        return builder.build()
    }

    fun addHeader(): Headers.Builder {
        return Headers.Builder()
    }

    fun addUA(ua: String): Headers {
        return addSingleHeader("user-agent", ua)
    }

    fun addCookie(cookie: String): Headers {
        return addSingleHeader("cookie", cookie)
    }

    fun addReferer(url: String): Headers {
        return addSingleHeader("referer", url)
    }

    fun addStream(byteString: ByteString): RequestBody {
        return byteString.toRequestBody(MEDIA_STREAM)
    }

    @Throws(IOException::class)
    fun addStream(url: String): RequestBody {
        return addStream(getByteStr(url))
    }

    @Throws(IOException::class)
    private fun download(url: String): Response {
        var redirectUrl: String = url
        var response: Response
        while (true) {
            response = OkHttpUtil[redirectUrl]
            val code: Int = response.code
            redirectUrl = if (code == 302 || code == 301) {
                response.close()
                response.header("location")!!
            } else break
        }
        return response
    }

    @Throws(IOException::class)
    fun downloadStr(url: String): String {
        val response: Response = download(url)
        return getStr(response)
    }

    @Throws(IOException::class)
    fun downloadBytes(url: String): ByteArray {
        val response: Response = download(url)
        return getBytes(response)
    }

    fun inputStreamClone(inputStream: InputStream): ByteArrayOutputStream? {
        return try {
            val baos = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var len: Int
            while (inputStream.read(buffer).also { len = it } != -1) {
                baos.write(buffer, 0, len)
            }
            baos.flush()
            baos
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}

enum class UA(val value: String) {
    PC("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.150 Safari/537.36"),
    MOBILE("Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.92 Mobile Safari/537.36"),
    QQ("Mozilla/5.0 (Linux; Android 10; V1914A Build/QP1A.190711.020; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/66.0.3359.126 MQQBrowser/6.2 TBS/045132 Mobile Safari/537.36 V1_AND_SQ_8.3.0_1362_YYB_D QQ/8.3.0.4480 NetType/4G WebP/0.3.0 Pixel/1080 StatusBarHeight/85 SimpleUISwitch/0 QQTheme/1000"),
    QQ2("Mozilla/5.0 (Linux; Android 10; V1914A Build/QP1A.190711.020; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/77.0.3865.120 MQQBrowser/6.2 TBS/045224 Mobile Safari/537.36 V1_AND_SQ_8.3.9_1424_YYB_D QQ/8.3.9.4635 NetType/4G WebP/0.3.0 Pixel/1080 StatusBarHeight/85 SimpleUISwitch/0 QQTheme/1000");
}