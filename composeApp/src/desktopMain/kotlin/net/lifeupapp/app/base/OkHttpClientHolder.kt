package base

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object OkHttpClientHolder {
    val okHttpClient = OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().apply {
        setLevel(HttpLoggingInterceptor.Level.BODY)
    }).build()

    var host: String = ""
        private set

    fun updateHost(ip: String, port: String) {
        host = "http://${ip}:${port}"
    }
}