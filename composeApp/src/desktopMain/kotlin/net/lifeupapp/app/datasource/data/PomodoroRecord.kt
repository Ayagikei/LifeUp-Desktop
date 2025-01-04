package net.lifeupapp.app.datasource.data

import kotlinx.serialization.Serializable

@Serializable
data class PomodoroRecord(
    val id: Long?,
    val startTime: Long?,
    val endTime: Long?,
    val duration: Long,
    val reward: Double
) {
    class Builder {
        private var id: Long? = null
        private var startTime: Long? = null
        private var endTime: Long? = null
        private var duration: Long = 0
        private var reward: Double = 0.0

        fun setId(id: Long?) = apply { this.id = id }
        fun setStartTime(startTime: Long?) = apply { this.startTime = startTime }
        fun setEndTime(endTime: Long?) = apply { this.endTime = endTime }
        fun setDuration(duration: Long) = apply { this.duration = duration }
        fun setReward(reward: Double) = apply { this.reward = reward }

        fun build(): PomodoroRecord {
            return PomodoroRecord(
                id = id,
                startTime = startTime,
                endTime = endTime,
                duration = duration,
                reward = reward
            )
        }
    }

    companion object {
        fun builder(block: Builder.() -> Unit): PomodoroRecord {
            return Builder().apply(block).build()
        }
    }
} 