package base

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object OkHttpClientHolder {
    private var apiToken: String = ""

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        })
        .addInterceptor(Interceptor { chain ->
            val original = chain.request()
            val request = if (apiToken.isNotEmpty()) {
                original.newBuilder()
                    .header("Authorization", apiToken)
                    .build()
            } else {
                original
            }
            chain.proceed(request)
        })
        .build()

    var host: String = ""
        private set

    fun updateHost(ip: String, port: String) {
        host = "http://${ip}:${port}"
    }

    fun updateApiToken(token: String) {
        apiToken = token
    }
}