package net.lifeupapp.app.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

object AppScope : CoroutineScope {
    private val job = SupervisorJob()

    // 默认使用 Main 调度器，因为是 UI 应用
    override val coroutineContext: CoroutineContext = job + Dispatchers.Main.immediate

    fun cleanup() {
        job.cancel()
    }
}