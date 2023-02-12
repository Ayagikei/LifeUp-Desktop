package datasource.data

import kotlinx.serialization.Serializable

@Serializable
data class AchievementCategory(
    val id: Long?,
    val name: String,
    val desc: String,
    val iconUri: String,
    val isAsc: Boolean,
    val sort: String,
    val filter: String,
    val order: Int,
    val type: Int
) {

    enum class AchievementType(val type: Int) {
        USER(0),
        SYSTEM(1)
    }

    class Builder {
        private var id: Long? = null
        private var name: String = ""
        private var desc: String = ""
        private var iconUri: String = ""
        private var isAsc: Boolean = false
        private var sort: String = ""
        private var filter: String = ""
        private var order: Int = 0
        private var type: Int = 0


        fun setId(id: Long?) = apply { this.id = id }
        fun setName(name: String) = apply { this.name = name }
        fun setDesc(notes: String) = apply { this.desc = notes }
        fun setIconUri(iconUri: String) = apply { this.iconUri = iconUri }
        fun setIsAsc(isAsc: Boolean) = apply { this.isAsc = isAsc }
        fun setSort(sort: String) = apply { this.sort = sort }
        fun setFilter(filter: String) = apply { this.filter = filter }
        fun setOrder(order: Int) = apply { this.order = order }
        fun setType(type: Int) = apply { this.type = type }


        fun build(): AchievementCategory {
            return AchievementCategory(
                id = id,
                name = name,
                desc = desc,
                iconUri = iconUri,
                isAsc = isAsc,
                sort = sort,
                filter = filter,
                order = order,
                type = type
            )
        }
    }

    companion object {
        fun builder(block: Builder.() -> Unit): AchievementCategory {
            return Builder().apply(block).build()
        }
    }
}