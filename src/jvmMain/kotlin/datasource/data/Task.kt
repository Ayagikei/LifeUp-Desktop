package datasource.data

import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val id: Long?,
    val name: String,
    val notes: String
) {
    class Builder {
        private var id: Long? = null
        private var name: String = ""
        private var notes: String = ""

        fun setId(id: Long?) = apply { this.id = id }
        fun setName(name: String) = apply { this.name = name }
        fun setNotes(notes: String) = apply { this.notes = notes }

        fun build(): Task {
            return Task(id, name, notes)
        }
    }

    companion object {
        fun builder(block: Builder.() -> Unit): Task {
            return Builder().apply(block).build()
        }
    }
}