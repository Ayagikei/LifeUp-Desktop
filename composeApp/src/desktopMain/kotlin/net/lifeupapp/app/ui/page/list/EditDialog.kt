package ui.page.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import base.json
import kotlinx.serialization.encodeToString
import ui.Strings
import ui.page.config.Spacer8dpH

@Composable
internal fun EditDialog(
    item: TodoItem,
    onCloseClicked: () -> Unit,
    onTextChanged: (String) -> Unit,
    onDoneChanged: (Boolean) -> Unit,
) {
    Dialog(
        title = "Edit todo",
        onCloseRequest = onCloseClicked,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(Strings.oops_wip)

            Spacer8dpH()

            Text(json.encodeToString(item.task))
//            TextField(
//                value = item.text,
//                modifier = Modifier.weight(1F).fillMaxWidth().sizeIn(minHeight = 192.dp),
//                label = { Text("Todo text") },
//                onValueChange = onTextChanged,
//            )

            Spacer(modifier = Modifier.height(8.dp))

//            Row {
//                Text(text = "Completed", Modifier.padding(15.dp))
//
//                Checkbox(
//                    checked = item.isDone,
//                    onCheckedChange = onDoneChanged,
//                )
//            }
        }
    }
}
