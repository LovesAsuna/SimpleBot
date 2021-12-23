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
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import kotlin.coroutines.Continuation


object Request {
    private const val timeout = 60000L
    private val client = HttpClient(OkHttp) {
        BrowserUserAgent()
        install(HttpTimeout)
    }

    private val mapper = jacksonObjectMapper()

    suspend fun get(url: String, timeout: Long = this.timeout, headers: Map<String, String>? = null): HttpResponse {
        return client.get(url) {
            timeout {
                requestTimeoutMillis = timeout
            }
            headers?.forEach { (key, value) ->
                header(key, value)
            }
        }
    }

    suspend fun getStr(url: String, timeout: Long = this.timeout, headers: Map<String, String>? = null): String {
        return get(url, timeout, headers).receive()
    }

    suspend fun getIs(url: String, timeout: Long = this.timeout, headers: Map<String, String>? = null): InputStream {
        return get(url, timeout, headers).getInputStream()
    }

    suspend fun getJson(url: String, timeout: Long = this.timeout, headers: Map<String, String>? = null): JsonNode {
        return get(url, timeout, headers).toJson()
    }

    suspend fun postText(
        url: String,
        text: String,
        timeout: Long = this.timeout,
        headers: Map<String, String>? = null
    ): HttpResponse {
        return client.post(url) {
            timeout {
                requestTimeoutMillis = timeout
            }
            headers?.forEach { (key, value) ->
                header(key, value)
            }
            body = text
        }
    }

    suspend fun postJson(
        url: String,
        jsonNode: JsonNode,
        timeout: Long = this.timeout,
        headers: Map<String, String>? = null
    ): HttpResponse {
        return client.post(url) {
            timeout {
                requestTimeoutMillis = timeout
            }
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
        timeout: Long = this.timeout,
        headers: Map<String, String>? = null
    ): HttpResponse {
        return client.post(url) {
            timeout {
                requestTimeoutMillis = timeout
            }
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
        timeout: Long = this.timeout,
        headers: Map<String, String>? = null
    ): HttpResponse {
        return client.submitForm(
            url,
            formParameters = Parameters.build {
                form.forEach { (key, value) ->
                    append(key, value)
                }
            },
            encodeInQuery = true
        ) {
            timeout {
                requestTimeoutMillis = timeout
            }
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
        timeout: Long = this.timeout,
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
        timeout: Long = this.timeout,
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
            timeout {
                requestTimeoutMillis = timeout
            }
            headers?.forEach { (key, value) ->
                header(key, value)
            }
        }
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
        println(HttpClientCall::class.java.getDeclaredMethod("receive", TypeInfo::class.java, Continuation::class.java))
        val str: String = this.receive()
        return mapper.readTree(str)
    }
}