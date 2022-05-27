package com.hyosakura.bot.util.network

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream


object Request {
    private const val TIMEOUT = 60 * 1000L
    private val client = HttpClient(OkHttp) {
        BrowserUserAgent()
        install(HttpTimeout)
        followRedirects = false
        expectSuccess = false
    }

    private val mapper = jacksonObjectMapper()

    private fun HttpRequestBuilder.setTimeout(timeout: Long) {
        timeout {
            requestTimeoutMillis = timeout
            socketTimeoutMillis = timeout
        }
    }

    suspend fun get(url: String, timeout: Long = this.TIMEOUT, headers: Map<String, String>? = null): HttpResponse =
        withContext(Dispatchers.IO) {
            client.get(url) {
                setTimeout(timeout)
                headers?.forEach { (key, value) ->
                    header(key, value)
                }
            }
        }

    suspend fun getStr(url: String, timeout: Long = this.TIMEOUT, headers: Map<String, String>? = null): String {
        return get(url, timeout, headers).receive()
    }

    suspend fun getIs(url: String, timeout: Long = this.TIMEOUT, headers: Map<String, String>? = null): InputStream {
        return get(url, timeout, headers).getInputStream()
    }

    suspend fun getJson(url: String, timeout: Long = this.TIMEOUT, headers: Map<String, String>? = null): JsonNode {
        return get(url, timeout, headers).toJson()
    }

    suspend fun postText(
        url: String,
        text: String,
        timeout: Long = this.TIMEOUT,
        headers: Map<String, String>? = null
    ): HttpResponse {
        return client.post(url) {
            setTimeout(timeout)
            headers?.forEach { (key, value) ->
                header(key, value)
            }
            body = text
        }
    }

    suspend fun postJson(
        url: String,
        jsonNode: JsonNode,
        timeout: Long = this.TIMEOUT,
        headers: Map<String, String>? = null
    ): HttpResponse {
        return client.post(url) {
            setTimeout(timeout)
            headers?.forEach { (key, value) ->
                header(key, value)
            }
            contentType(ContentType.Application.Json)
            body = jsonNode.toString()
        }
    }

    suspend fun postJson(
        url: String,
        jsonText: String,
        timeout: Long = this.TIMEOUT,
        headers: Map<String, String>? = null
    ): HttpResponse {
        return client.post(url) {
            setTimeout(timeout)
            headers?.forEach { (key, value) ->
                header(key, value)
            }
            contentType(ContentType.Application.Json)
            body = jsonText
        }
    }

    suspend fun submitForm(
        url: String,
        form: Map<String, String>,
        timeout: Long = this.TIMEOUT,
        encodeInQuery: Boolean = false,
        headers: Map<String, String>? = null
    ): HttpResponse {
        return client.submitForm(
            url,
            formParameters = Parameters.build {
                form.forEach { (key, value) ->
                    append(key, value)
                }
            },
            encodeInQuery = encodeInQuery
        ) {
            setTimeout(timeout)
            headers?.forEach { (key, value) ->
                header(key, value)
            }
        }
    }

    suspend fun submitFormWithFile(
        url: String,
        form: Map<String, String>,
        file: File,
        fileKey: String = file.name,
        timeout: Long = this.TIMEOUT,
        headers: Map<String, String>? = null
    ): HttpResponse {
        return submitFormWithBinaryData(
            url,
            form,
            file.readBytes(),
            fileKey,
            {
                append(HttpHeaders.ContentDisposition, "filename=${file.name}")
            },
            timeout,
            headers
        )
    }

    suspend fun submitFormWithBinaryData(
        url: String,
        form: Map<String, String>,
        byteArray: ByteArray,
        binaryKey: String,
        binaryHeaderBuilder: HeadersBuilder.() -> Unit,
        timeout: Long = this.TIMEOUT,
        headers: Map<String, String>? = null
    ): HttpResponse {
        return client.submitFormWithBinaryData(
            url,
            formData = formData {
                form.forEach { (key, value) ->
                    append(key, value)
                }
                append(binaryKey, byteArray, Headers.build {
                    binaryHeaderBuilder()
                })
            }
        ) {
            setTimeout(timeout)
            headers?.forEach { (key, value) ->
                header(key, value)
            }
        }
    }

    fun HttpResponse.getCookie(): String {
        val builder = StringBuilder()
        for (header in headers.getAll("Set-Cookie")!!) {
            if (header.contains("deleted")) {
                continue
            }
            val cookie = header.substringBefore(';')
            val arr = cookie.split("=")
            if (arr.size < 2 || arr[1] == ";") {
                continue
            }
            builder.append(cookie).append("; ")
        }
        return builder.toString()
    }

    fun InputStream.clone(): ByteArrayOutputStream {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        var len: Int
        while (this.read(buffer).also { len = it } > -1) {
            byteArrayOutputStream.write(buffer, 0, len)
        }
        byteArrayOutputStream.flush()
        return byteArrayOutputStream
    }

    suspend fun HttpResponse.getInputStream(): InputStream = this.receive()

    suspend fun HttpResponse.toJson(): JsonNode {
        val `is` = getInputStream()
        return mapper.readTree(`is`)
    }
}