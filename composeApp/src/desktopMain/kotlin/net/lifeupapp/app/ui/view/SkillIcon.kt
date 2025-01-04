import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.dp
import lifeupdesktop.composeapp.generated.resources.*
import net.lifeupapp.app.datasource.data.Skill
import net.lifeupapp.app.ui.view.AsyncImage
import net.lifeupapp.app.ui.view.loadImageBitmap
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun SkillIcon(
    skill: Skill,
    modifier: Modifier = Modifier.size(40.dp),
    selected: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    val iconModifier = if (onClick != null) {
        modifier.clickable { onClick() }
    } else {
        modifier
    }

    val colorFilter = if (selected) null else ColorFilter.colorMatrix(
        ColorMatrix().apply { setToSaturation(0f) }
    )

    if (skill.type > Skill.SkillType.USER.type && skill.icon.isBlank()) {
        Image(
            painter = painterResource(getLocalIconFilePathBySkillType(skill.type)),
            contentDescription = skill.name ?: "skill icon",
            modifier = iconModifier,
            colorFilter = colorFilter
        )
    } else {
        AsyncImage(
            condition = skill.icon.isNotBlank(),
            load = {
                loadImageBitmap(skill.icon)
            },
            painterFor = {
                remember { BitmapPainter(it) }
            },
            contentDescription = skill.name ?: "skill icon",
            modifier = iconModifier,
            colorFilter = colorFilter,
            onError = {
                Image(
                    painter = painterResource(Res.drawable.ic_pic_loading_cir),
                    contentDescription = "skill icon",
                    modifier = iconModifier,
                    colorFilter = colorFilter
                )
            }
        )
    }
}

fun getLocalIconFilePathBySkillType(type: Int): DrawableResource {
    return when (type) {
        Skill.SkillType.DEFAULT_CHARM.type -> Res.drawable.ic_attr_charm
        Skill.SkillType.DEFAULT_CREATIVE.type -> Res.drawable.ic_attr_creative
        Skill.SkillType.DEFAULT_ENDURANCE.type -> Res.drawable.ic_attr_endurance
        Skill.SkillType.DEFAULT_LEARNING.type -> Res.drawable.ic_attr_learning
        Skill.SkillType.DEFAULT_STRENGTH.type -> Res.drawable.ic_attr_strength
        Skill.SkillType.DEFAULT_VITALITY.type -> Res.drawable.ic_attr_vitality
        else -> Res.drawable.ic_pic_loading_cir
    }
}