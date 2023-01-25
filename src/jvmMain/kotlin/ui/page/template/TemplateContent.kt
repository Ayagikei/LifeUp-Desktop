package ui.page.template

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun TemplateContent(modifier: Modifier = Modifier) {
    val model = remember { TemplateStore() }
    val state = model.state

}