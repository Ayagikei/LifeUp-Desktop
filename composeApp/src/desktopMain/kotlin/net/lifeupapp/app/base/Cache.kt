package net.lifeupapp.app.base

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.lifeupapp.app.utils.logger

/**
 * 一个简单的内存缓存实现，主要用作网络异常时的后备方案
 */
class Cache<T>(
    private val loader: suspend () -> T
) {
    private var data: T? = null
    private val mutex = Mutex()
    private val log = logger

    /**
     * 获取数据，优先尝试重新加载，如果失败则使用缓存
     */
    suspend fun get(): T {
        return mutex.withLock {
            try {
                // 每次都尝试重新加载
                val freshData = loader()
                data = freshData  // 更新缓存
                freshData
            } catch (e: Exception) {
                log.warn(e) { "Failed to load fresh data, falling back to cache" }
                // 如果加载失败且有缓存，则使用缓存
                data?.let { return@withLock it }
                // 如果没有缓存，则抛出异常
                throw e
            }
        }
    }

    /**
     * 强制刷新数据
     */
    suspend fun refresh(): T {
        return mutex.withLock {
            val freshData = loader()
            data = freshData
            freshData
        }
    }

    /**
     * 清除缓存
     */
    fun clear() {
        data = null
    }
} 