package net.lifeupapp.app.datasource.net

class HttpException : RuntimeException {
    val error: ApiError

    constructor(error: ApiError) {
        this.error = error
    }

    constructor(httpResponse: HttpResponse<*>) {
        this.error = when (httpResponse.code) {
            401 -> ApiError.AuthenticationError
            HttpResponse.LIFEUP_NOT_RUNNING -> ApiError.LifeUpNotRunning
            else -> ApiError.UnknownError(httpResponse.code)
        }
    }

    override val message: String?
        get() = "Unexpected http error: $error"
}