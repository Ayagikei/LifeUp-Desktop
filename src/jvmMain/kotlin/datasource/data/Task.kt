package datasource.data

import kotlinx.serialization.Serializable


@Serializable
data class Task(
    val id: Long?,
    val gid: Long?,
    val name: String,
    val notes: String,
    val status: Int,
    val startTime: Long,
    val deadline: Long,
    val remindTime: Long,
    val frequency: Int,
    val exp: Int,
    val skillIds: List<Long>,
    val coin: Long,
    val coinVariable: Long,
    val itemId: Long,
    val words: String,
    val categoryId: Long,
    val order: Int
) {
    class Builder {
        private var id: Long? = null
        private var gid: Long? = null
        private var name: String = ""
        private var notes: String = ""
        private var status: Int = 0
        private var startTime: Long = 0
        private var deadline: Long? = null
        private var remindTime: Long? = null
        private var frequency: Int = 0
        private var exp: Int = 0
        private var skillIds: List<Long> = emptyList()
        private var coin: Long = 0
        private var coinVariable: Long = 0
        private var itemId: Long? = null
        private var itemAmount: Int = 0
        private var words: String = ""
        private var categoryId: Long? = null
        private var order: Int = 0

        fun setId(id: Long?) = apply { this.id = id }

        fun setGid(gid: Long?) = apply { this.gid = gid }
        fun setName(name: String) = apply { this.name = name }
        fun setNotes(notes: String) = apply { this.notes = notes }

        fun setStatus(status: Int) = apply { this.status = status }

        fun setStartTime(startTime: Long) = apply { this.startTime = startTime }

        fun setDeadline(deadline: Long?) = apply { this.deadline = deadline }

        fun setRemindTime(remindTime: Long?) = apply { this.remindTime = remindTime }

        fun setFrequency(frequency: Int) = apply { this.frequency = frequency }

        fun setExp(exp: Int) = apply { this.exp = exp }

        fun setSkillIds(skillIds: List<Long>) = apply { this.skillIds = skillIds }

        fun setCoin(coin: Long) = apply { this.coin = coin }

        fun setCoinVariable(coinVariable: Long) = apply { this.coinVariable = coinVariable }

        fun setItemId(itemId: Long?) = apply { this.itemId = itemId }

        fun setItemAmount(itemAmount: Int) = apply { this.itemAmount = itemAmount }

        fun setWords(words: String) = apply { this.words = words }

        fun setCategoryId(categoryId: Long?) = apply { this.categoryId = categoryId }

        fun setOrder(order: Int) = apply { this.order = order }

        fun build(): Task {
            return Task(
                id = id,
                gid = gid,
                name = name,
                notes = notes,
                status = status,
                startTime = startTime,
                deadline = deadline ?: 0,
                remindTime = remindTime ?: 0,
                frequency = frequency,
                exp = exp,
                skillIds = skillIds,
                coin = coin,
                coinVariable = coinVariable,
                itemId = itemId ?: 0,
                words = words,
                categoryId = categoryId ?: 0,
                order = order
            )
        }
    }

    companion object {
        fun builder(block: Builder.() -> Unit): Task {
            return Builder().apply(block).build()
        }
    }
}