package datasource.net

class HttpException(
    private val httpResponse: HttpResponse<*>
) : RuntimeException() {
    override val message: String?
        get() = "Unexpected http response: $httpResponse"
}