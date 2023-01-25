package base

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import ui.GlobalStore

object OkHttpClientHolder {
    val okHttpClient = OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().apply {
        setLevel(HttpLoggingInterceptor.Level.BODY)
    }).build()

    val host: String
        get() = "http://${GlobalStore.ip}:${GlobalStore.port}"
}