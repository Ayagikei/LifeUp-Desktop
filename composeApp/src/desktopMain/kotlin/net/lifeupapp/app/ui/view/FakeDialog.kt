package ui.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import ui.DialogStatus
import ui.theme.dialogTitle

@OptIn(ExperimentalUnitApi::class)
@Preview
@Composable
fun previewFakeDialog() {
    fakeDialog(DialogStatus("Title", "Message", positiveButton = "Yes", negativeButton = "Cancel"))
}

@ExperimentalUnitApi
@Composable
fun fakeDialog(dialogStatus: DialogStatus) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f))
            .clickable(interactionSource = interactionSource, indication = null) {

            }, contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(0.55f).wrapContentSize()
                .background(Color.White, shape = RoundedCornerShape(16.dp))
        ) {
            Column(
                Modifier.wrapContentSize().padding(16.dp),
            ) {
                Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        dialogStatus.title,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.dialogTitle
                    )
                }
                Spacer(Modifier.height(24.dp))
                Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        dialogStatus.message,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body2
                    )
                }
                Spacer(Modifier.height(24.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = dialogStatus.negativeAction, modifier = Modifier.wrapContentSize()) {
                        Text(dialogStatus.negativeButton)
                    }
                    TextButton(onClick = dialogStatus.positiveAction, modifier = Modifier.wrapContentSize()) {
                        Text(dialogStatus.positiveButton)
                    }
                }
            }
        }
    }
}