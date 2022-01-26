package io.realm.kotlin.demo.util

// Remove when Kotlin's Closeable is supported in K/N https://youtrack.jetbrains.com/issue/KT-31066
// Alternatively use Ktor Closeable which is K/N ready.
interface Closeable {
    fun close()
}