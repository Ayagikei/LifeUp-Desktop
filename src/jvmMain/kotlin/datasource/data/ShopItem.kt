package net.lifeupapp.lifeup.api.content.shop

import kotlinx.serialization.Serializable


@Serializable
data class ShopItem(
    val id: Long?,
    val name: String,
    val desc: String,
    val icon: String,
    val categoryId: Long,
    val stockNumber: Int,
    val ownNumber: Int,
    val price: Long,
    val order: Int,
    val disablePurchase: Boolean
) {
    class Builder {
        private var id: Long? = null
        private var name: String = ""
        private var desc: String = ""
        private var icon: String = ""
        private var categoryId: Long? = null
        private var stockNumber: Int = 0
        private var ownNumber: Int = 0
        private var price: Long = 0
        private var order: Int = 0
        private var disablePurchase: Boolean = false

        fun setId(id: Long?) = apply { this.id = id }
        fun setName(name: String) = apply { this.name = name }
        fun setDesc(notes: String) = apply { this.desc = notes }
        fun setIconUri(icon: String) = apply { this.icon = icon }
        fun setCategoryId(categoryId: Long?) = apply { this.categoryId = categoryId }
        fun setStockNumber(stockNumber: Int) = apply { this.stockNumber = stockNumber }
        fun setOwnNumber(ownNumber: Int) = apply { this.ownNumber = ownNumber }
        fun setPrice(price: Long) = apply { this.price = price }
        fun setOrder(order: Int) = apply { this.order = order }
        fun setDisablePurchase(disablePurchase: Boolean) =
            apply { this.disablePurchase = disablePurchase }

        fun build(): ShopItem {
            return ShopItem(
                id = id,
                name = name,
                desc = desc,
                icon = icon,
                categoryId = categoryId ?: 0,
                stockNumber = stockNumber,
                ownNumber = ownNumber,
                price = price,
                order = order,
                disablePurchase = disablePurchase
            )
        }
    }

    companion object {
        fun builder(block: Builder.() -> Unit): ShopItem {
            return Builder().apply(block).build()
        }
    }
}