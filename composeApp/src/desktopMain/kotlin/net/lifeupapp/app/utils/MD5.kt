package utils

import java.security.MessageDigest


var hexDigits = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')

fun String.md5(): String {
    val stringToHash = this
    val messageDigest: MessageDigest = MessageDigest.getInstance("MD5")
    messageDigest.update(stringToHash.toByteArray())
    val bytes: ByteArray = messageDigest.digest()
    val sb = StringBuilder(bytes.size * 2)
    for (b in bytes) {
        sb.append(hexDigits[b.toInt() shr 4 and 0x0f])
        sb.append(hexDigits[b.toInt() and 0x0f])
    }
    return sb.toString()
}