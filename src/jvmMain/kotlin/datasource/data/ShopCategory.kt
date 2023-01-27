package datasource.data

import kotlinx.serialization.Serializable


@Serializable
data class ShopCategory(
    val id: Long?,
    val name: String,
    val isAsc: Boolean,
    val sort: String,
    val order: Int
) {
    class Builder {
        private var id: Long? = null
        private var name: String = ""
        private var isAsc: Boolean = false
        private var sort: String = ""
        private var order: Int = 0


        fun setId(id: Long?) = apply { this.id = id }
        fun setName(name: String) = apply { this.name = name }
        fun setIsAsc(isAsc: Boolean) = apply { this.isAsc = isAsc }
        fun setSort(sort: String) = apply { this.sort = sort }
        fun setOrder(order: Int) = apply { this.order = order }


        fun build(): ShopCategory {
            return ShopCategory(
                id = id,
                name = name,
                isAsc = isAsc,
                sort = sort,
                order = order,
            )
        }
    }

    companion object {
        fun builder(block: Builder.() -> Unit): ShopCategory {
            return Builder().apply(block).build()
        }
    }
}