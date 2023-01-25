package ui.page.config

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ui.GlobalStore
import ui.Strings
import ui.theme.unimportantText

@Composable
fun ConfigContent(modifier: Modifier = Modifier) {
    Column(
        modifier.padding(24.dp)
    ) {
        Subtitle(Strings.base_config)
        Spacer16dp()
        Text(
            text = Strings.base_config_desc, color = MaterialTheme.colors.unimportantText
        )
        Spacer16dp()
        TextField(modifier = modifier.fillMaxWidth(), value = GlobalStore.ip, onValueChange = {
            GlobalStore.updateIpOrPort(ip = it)
        }, label = { Text(Strings.ip_address) })
        Spacer16dp()
        TextField(modifier = modifier.fillMaxWidth(), value = GlobalStore.port, onValueChange = {
            GlobalStore.updateIpOrPort(port = it)
        }, label = { Text(Strings.server_port) })
        Spacer16dp()
        val coin = GlobalStore.coinValue

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (coin == null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colors.error
                    )
                    Text(modifier = Modifier.padding(start = 8.dp), text = Strings.not_connected)
                }
                Button(onClick = {
                    GlobalStore.fetchCoin()
                }, modifier = Modifier.padding(start = 8.dp)) {
                    Text(Strings.test_connection)
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colors.secondary
                    )
                    Text(modifier = Modifier.padding(start = 8.dp), text = Strings.connected.format(coin))
                }
                OutlinedButton(onClick = {
                    GlobalStore.fetchCoin()
                }, modifier = Modifier.padding(start = 8.dp)) {
                    Text(Strings.test_connection)
                }
            }
        }
    }
}

@Composable
fun Subtitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.subtitle1.copy(color = MaterialTheme.colors.primary),
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun Spacer16dp() {
    Spacer(Modifier.height(16.dp))
}


@Preview
@Composable
fun configContentPreview() {
    ConfigContent()
}