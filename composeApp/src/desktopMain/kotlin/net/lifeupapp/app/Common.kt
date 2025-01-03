import kotlinx.coroutines.GlobalScope
import java.util.logging.Logger

val AppScope = GlobalScope

val Any?.logger: Logger
    get() = Logger.getLogger((this?.javaClass ?: Any::class.java).toGenericString())