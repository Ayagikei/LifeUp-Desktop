package datasource.data

import kotlinx.serialization.Serializable


@Serializable
data class Achievement(
    val id: Long?,
    val name: String,
    val desc: String,
    val iconUri: String,
    val categoryId: Long,
    val status: Int,
    val exp: Int,
    val coin: Long,
    val coinVariable: Long,
    val type: Int,
    val progress: Int,
    val order: Int,
    val itemId: Long,
    val itemAmount: Int,
    val unlockedTime: Long
) {
    class Builder {
        private var id: Long? = null
        private var name: String = ""
        private var desc: String = ""
        private var iconUri: String = ""
        private var categoryId: Long = 0
        private var status: Int = 0
        private var exp: Int = 0
        private var coin: Long = 0
        private var coinVariable: Long = 0
        private var type: Int = 0
        private var progress: Int = 0
        private var order: Int = 0
        private var itemId: Long = 0
        private var itemAmount: Int = 0
        private var unlockedTime: Long = 0


        fun setId(id: Long?) = apply { this.id = id }
        fun setName(name: String) = apply { this.name = name }
        fun setDesc(notes: String) = apply { this.desc = notes }
        fun setIconUri(iconUri: String) = apply { this.iconUri = iconUri }
        fun setCategoryId(categoryId: Long) = apply { this.categoryId = categoryId }
        fun setStatus(status: Int) = apply { this.status = status }
        fun setExp(exp: Int) = apply { this.exp = exp }
        fun setCoin(coin: Long) = apply { this.coin = coin }
        fun setCoinVariable(coinVariable: Long) = apply { this.coinVariable = coinVariable }
        fun setType(type: Int) = apply { this.type = type }
        fun setProgress(progress: Int) = apply { this.progress = progress }
        fun setOrder(order: Int) = apply { this.order = order }
        fun setItemId(itemId: Long) = apply { this.itemId = itemId }
        fun setItemAmount(itemAmount: Int) = apply { this.itemAmount = itemAmount }

        fun setUnlockedTime(unlockedTime: Long) = apply { this.unlockedTime = unlockedTime }


        fun build(): Achievement {
            return Achievement(
                id = id,
                name = name,
                desc = desc,
                iconUri = iconUri,
                categoryId = categoryId,
                status = status,
                exp = exp,
                coin = coin,
                coinVariable = coinVariable,
                type = type,
                progress = progress,
                order = order,
                itemId = itemId,
                itemAmount = itemAmount,
                unlockedTime = unlockedTime
            )
        }
    }

    companion object {
        fun builder(block: Builder.() -> Unit): Achievement {
            return Builder().apply(block).build()
        }
    }
}