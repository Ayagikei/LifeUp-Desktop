package net.lifeupapp.app.base

object Val {
    object AppVersion {
        private const val UNKNOWN = "UNKNOWN"
        private val versionSources = listOf(
            { System.getProperty("jpackage.app-version") },
            { System.getenv("APP_VERSION") },
            { getVersionFromManifest() },
            { getVersionFromResourceFile() }
        )

        val version: String by lazy {
            versionSources.firstNotNullOfOrNull { it() } ?: UNKNOWN
        }

        private fun getVersionFromManifest(): String? {
            return javaClass.getPackage()?.implementationVersion
        }

        private fun getVersionFromResourceFile(): String? {
            return javaClass.getResourceAsStream("/version.txt")?.bufferedReader()
                ?.use { it.readLine() }
        }

        fun isUnknown() = version == UNKNOWN
    }


    val versionCode: Int by lazy {
        val version = AppVersion.version.split(".")
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

    const val targetLifeUpCloudVersion = "2.0.0+"

    const val targetLifeUpAndroidVersion = "1.98.0+"
}