package base

import kotlinx.serialization.json.Json

val json = Json {
    ignoreUnknownKeys = true
}