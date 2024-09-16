package net.lifeupapp.app.base

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

inline fun CoroutineScope.launchSafely(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    crossinline block: suspend CoroutineScope.() -> Unit
) {
    launch(SupervisorJob() + context + handle, start) {
        block()
    }
}

val handle = CoroutineExceptionHandler { _, throwable ->
    throwable.printStackTrace()
}