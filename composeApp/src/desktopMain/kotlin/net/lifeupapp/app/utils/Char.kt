package net.lifeupapp.app.utils

@JvmName("ifNullOrBlankCharSequence")
inline fun <C : CharSequence, R> C?.ifNullOrBlank(defaultValue: () -> R): R {
    return if (isNullOrBlank()) defaultValue() else this as R
}