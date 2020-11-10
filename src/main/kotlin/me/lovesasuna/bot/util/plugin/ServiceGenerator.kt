package me.lovesasuna.bot.util.plugin

import me.lovesasuna.bot.data.BotData
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

object ServiceGenerator {
    private val retrofitBuilder: Retrofit.Builder = Retrofit.Builder()

    fun <T> create(api: Class<T>, baseurl: String, interceptor: Interceptor? = null): T {
        val clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
        interceptor?.let { clientBuilder.addInterceptor(it) }
        return retrofitBuilder
                .baseUrl(baseurl)
                .client(clientBuilder.build())
                .addConverterFactory(JacksonConverterFactory.create(BotData.objectMapper))
                .build().create(api)
    }
}