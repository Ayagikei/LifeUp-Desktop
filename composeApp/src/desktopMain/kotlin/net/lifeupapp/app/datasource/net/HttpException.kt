package datasource.net

import net.lifeupapp.app.datasource.net.HttpResponse

class HttpException(
    private val httpResponse: HttpResponse<*>
) : RuntimeException() {
    override val message: String?
        get() = "Unexpected http response: $httpResponse"
}