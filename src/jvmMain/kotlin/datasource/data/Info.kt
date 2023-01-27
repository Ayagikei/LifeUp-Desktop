package net.lifeupapp.lifeup.api.content.info

import kotlinx.serialization.Serializable


@Serializable
data class Info(
    val appVersion: Int,
    val appVersionName: String,
    val apiVersion: Int
) {
    class Builder {
        private var appVersion: Int = 0
        private var appVersionName: String = ""
        private var apiVersion: Int = 0

        fun setAppVersion(appVersion: Int) = apply { this.appVersion = appVersion }
        fun setAppVersionName(appVersionName: String) = apply { this.appVersionName = appVersionName }
        fun setApiVersion(apiVersion: Int) = apply { this.apiVersion = apiVersion }

        fun build(): Info {
            return Info(
                appVersion = appVersion,
                appVersionName = appVersionName,
                apiVersion = apiVersion
            )
        }
    }

    companion object {
        fun builder(block: Builder.() -> Unit): Info {
            return Builder().apply(block).build()
        }
    }
}