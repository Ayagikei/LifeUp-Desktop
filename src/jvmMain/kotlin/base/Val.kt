package base

object Val {
    // FIXME: get version from gradle
    val version: String
        get() = System.getProperty("jpackage.app-version") ?: "UNKNOWN"

    val versionCode: Int by lazy {
        val version = version.split(".")
        assert(version.size == 3) { "Invalid version format" }
        if (version.size != 3) {
            return@lazy 0
        }
        val major = version[0].padStart(2, '0').toIntOrNull() ?: 0
        val minor = version[1].padStart(2, '0').toIntOrNull() ?: 0
        val patch = version[2].padStart(2, '0').toIntOrNull() ?: 0
        assert(major <= 99 && minor <= 99 && patch <= 99) { "Version number is too large" }
        (major * 100000 + minor * 1000 + patch)
    }

    const val targetLifeUpCloudVersion = "1.2.0+"

    const val targetLifeUpAndroidVersion = "1.91.3+"
}