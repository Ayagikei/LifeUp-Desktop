package net.lifeupapp.app.datasource.net

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class ApiBatchResult(
    val url: String,
    val result: Map<String, JsonElement>
) {
    // 将 result Map 转换为 ApiResult
    val apiResult: ApiResult by lazy {
        ApiResult(result)
    }
}

sealed class ApiError {
    data object NetworkError : ApiError()
    data object AuthenticationError : ApiError()
    data object LifeUpNotRunning : ApiError()
    data class ServerError(val msg: String) : ApiError()
    data class UnknownError(val code: Int) : ApiError()
}

@Serializable
data class HttpResponse<T>(
    val code: Int,
    val message: String,
    val data: T?
) {
    companion object {
        const val SUCCESS = 200
        const val ERROR = 500
        const val LIFEUP_NOT_RUNNING = 10001

        fun <T> success(data: T?): HttpResponse<T?> {
            return HttpResponse(SUCCESS, "success", data)
        }

        fun <T> error(message: String): HttpResponse<T?> {
            return HttpResponse(ERROR, message, null)
        }

        fun <T> error(throwable: Throwable): HttpResponse<T?> {
            return HttpResponse(ERROR, throwable.message ?: "unknown error", null)
        }
    }

    fun dataOrThrow(): T {
        if (code != SUCCESS || data == null) {
            throw HttpException(this)
        }
        return data
    }

    fun successOrThrow(): T? {
        if (code != SUCCESS) {
            throw HttpException(this)
        }
        return data
    }

    fun onSuccess(block: (T?) -> Unit): HttpResponse<T> {
        if (code == SUCCESS) {
            block(data)
        }
        return this
    }

    fun onError(block: (String) -> Unit): HttpResponse<T> {
        if (code != SUCCESS) {
            block(message)
        }
        return this
    }
}

inline fun <reified T : Any> T.wrapAsResponse(): HttpResponse<T?> {
    return HttpResponse.success(this)
}
