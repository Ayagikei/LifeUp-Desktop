package datasource.data

import kotlinx.serialization.Serializable


@Serializable
data class Skill(
    val id: Long?,
    val name: String,
    val desc: String,
    val icon: String,
    val order: Int,
    val color: Int,
    val exp: Int,
    val level: Int,
    val untilNextLevelExp: Int,
    val currentLevelExp: Int,
    val type: Int
) {

    enum class SkillType(val type: Int) {
        USER(0),
        DEFAULT_STRENGTH(1),
        DEFAULT_LEARNING(2),
        DEFAULT_CHARM(3),
        DEFAULT_ENDURANCE(4),
        DEFAULT_VITALITY(5),
        DEFAULT_CREATIVE(6)
    }

    class Builder {
        private var id: Long? = null
        private var name: String = ""
        private var desc: String = ""
        private var icon: String = ""
        private var order: Int = 0
        private var color: Int = 0
        private var exp: Int = 0
        private var level: Int = 1
        private var untilNextLevelExp: Int = 0
        private var currentLevelExp: Int = 0
        private var type: Int = 0

        fun setId(id: Long?) = apply { this.id = id }
        fun setName(name: String) = apply { this.name = name }
        fun setDesc(desc: String) = apply { this.desc = desc }
        fun setIconUri(icon: String) = apply { this.icon = icon }
        fun setOrder(order: Int) = apply { this.order = order }
        fun setColorInt(color: Int) =
            apply { this.color = color }

        fun setExp(exp: Int) = apply { this.exp = exp }
        fun setLevel(level: Int) = apply { this.level = level }
        fun setUntilNextLevelExp(untilNextLevelExp: Int) =
            apply { this.untilNextLevelExp = untilNextLevelExp }

        fun setCurrentLevelExp(currentLevelExp: Int) =
            apply { this.currentLevelExp = currentLevelExp }

        fun setType(type: Int) = apply { this.type = type }

        fun build(): Skill {
            return Skill(
                id = id,
                name = name,
                desc = desc,
                icon = icon,
                order = order,
                color = color,
                exp, level, untilNextLevelExp, currentLevelExp, type
            )
        }
    }

    companion object {
        fun builder(block: Builder.() -> Unit): Skill {
            return Builder().apply(block).build()
        }
    }
}