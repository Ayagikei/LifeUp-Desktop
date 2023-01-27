package net.lifeupapp.lifeup.api.content.feelings


import kotlinx.serialization.Serializable

@Serializable
data class Feelings(
    val id: Long?,
    val content: String,
    val isFav: Boolean,
    val title: String,
    val time: Long,
    val attachments: List<String>,
    val type: Int
) {

    enum class FeelingType(val value: Int) {
        TASKS(0),
        ACHIEVEMENTS(1)
    }

    class Builder {
        private var id: Long? = null
        private var content: String = ""
        private var isFav: Boolean = false
        private var title: String = ""
        private var time: Long = 0
        private var attachments: List<String> = emptyList()
        private var type: Int = 0

        fun setId(id: Long?) = apply { this.id = id }
        fun setContent(content: String) = apply { this.content = content }
        fun setIsFav(isFav: Boolean) = apply { this.isFav = isFav }
        fun setTitle(title: String) = apply { this.title = title }
        fun setTime(time: Long) = apply { this.time = time }
        fun setAttachments(attachments: List<String>) = apply { this.attachments = attachments }
        fun setType(type: Int) = apply { this.type = type }

        fun build(): Feelings {
            return Feelings(
                id = id,
                content = content,
                isFav = isFav,
                title = title,
                time = time,
                attachments = attachments,
                type = type
            )
        }
    }

    companion object {
        fun builder(block: Builder.() -> Unit): Feelings {
            return Builder().apply(block).build()
        }
    }
}