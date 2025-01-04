package net.lifeupapp.app.datasource.data

import kotlinx.serialization.Serializable

@Serializable
data class RewardItem(
    val itemId: Long,
    val itemCount: Int
)
