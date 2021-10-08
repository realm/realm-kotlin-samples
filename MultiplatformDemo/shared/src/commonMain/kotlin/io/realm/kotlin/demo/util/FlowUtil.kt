package io.realm.kotlin.demo.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * Wrapper to consume Flow based API from Obj-C/Swift
 * Credit - https://github.com/JetBrains/kotlinconf-app/blob/master/common/src/mobileMain/kotlin/org/jetbrains/kotlinconf/FlowUtils.kt
 */
class CommonFlow<T>(private val origin: Flow<T>) : Flow<T> by origin {
    fun watch(block: (T) -> Unit): Closeable {
        val job = Job()
        onEach {
            block(it)
        }.launchIn(CoroutineScope(Dispatchers.Main + job))

        return object : Closeable {
            override fun close() {
                job.cancel()
            }
        }
    }
}
// Helper extension
internal fun <T> Flow<T>.asCommonFlow(): CommonFlow<T> = CommonFlow(this)
