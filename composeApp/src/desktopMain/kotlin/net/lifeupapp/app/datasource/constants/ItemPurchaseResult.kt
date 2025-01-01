package net.lifeupapp.app.datasource.constants

enum class ItemPurchaseResult(val code: Int) {
    PurchaseSuccess(0),
    DatabaseError(1),
    NotEnoughCoin(2),
    ItemNotFound(3),
    PurchaseAndUseSuccess(4),
    PurchaseSuccessAndUseFailure(5);

    companion object {
        fun fromCode(code: Int): ItemPurchaseResult {
            return entries.find { it.code == code }
                ?: throw IllegalArgumentException("Unknown purchase result code: $code")
        }
    }
} 