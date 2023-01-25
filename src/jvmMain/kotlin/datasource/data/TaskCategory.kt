package net.lifeupapp.lifeup.api.content.tasks.category

import kotlinx.serialization.Serializable


@Serializable
data class TaskCategory(
    val id: Long?,
    val name: String,
    val isAsc: Boolean,
    val sort: String,
    val filter: String,
    val order: Int,
    val status: Int,
    val type: Int
) {
    class Builder {
        private var id: Long? = null
        private var name: String = ""
        private var isAsc: Boolean = false
        private var sort: String = ""
        private var filter: String = ""
        private var order: Int = 0
        private var status: Int = 0
        private var type: Int = 0


        fun setId(id: Long?) = apply { this.id = id }
        fun setName(name: String) = apply { this.name = name }
        fun setIsAsc(isAsc: Boolean) = apply { this.isAsc = isAsc }
        fun setSort(sort: String) = apply { this.sort = sort }
        fun setFilter(filter: String) = apply { this.filter = filter }
        fun setOrder(order: Int) = apply { this.order = order }
        fun setStatus(status: Int) = apply { this.status = status }
        fun setType(type: Int) = apply { this.type = type }


        fun build(): TaskCategory {
            return TaskCategory(
                id = id,
                name = name,
                isAsc = isAsc,
                sort = sort,
                filter = filter,
                order = order,
                type = type,
                status = status
            )
        }
    }

    companion object {
        fun builder(block: Builder.() -> Unit): TaskCategory {
            return Builder().apply(block).build()
        }
    }
}