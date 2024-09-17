package net.lifeupapp.app.utils

@kotlin.jvm.JvmName("ifNullOrBlankCharSequence")
public inline fun <C : CharSequence, R> C?.ifNullOrBlank(defaultValue: () -> R): R {
    return if (isNullOrBlank()) defaultValue() else this as R
}