package ui.page.config

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import base.Val
import ui.AppStore
import ui.Strings
import ui.page.list.VerticalScrollbar
import ui.theme.unimportantText

@Composable
fun ConfigScreen(modifier: Modifier = Modifier) {
    val globalStore = AppStore.current
    val scrollState = rememberScrollState()
    Box {
        Column(
            modifier.padding(24.dp).verticalScroll(scrollState),
        ) {
            Subtitle(Strings.base_config)
            Spacer24dpH()
            Text(
                text = Strings.base_config_desc, color = MaterialTheme.colors.unimportantText
            )
            Spacer16dpH()
            TextField(modifier = modifier.fillMaxWidth(), value = globalStore.ip, onValueChange = {
                globalStore.updateIpOrPort(ip = it)
            }, label = { Text(Strings.ip_address) })
            Spacer16dpH()
            TextField(modifier = modifier.fillMaxWidth(), value = globalStore.port, onValueChange = {
                globalStore.updateIpOrPort(port = it)
            }, label = { Text(Strings.server_port) })
            Spacer16dpH()
            val coin = globalStore.coinValue

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
                        globalStore.fetchCoin()
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
                        globalStore.fetchCoin()
                    }, modifier = Modifier.padding(start = 8.dp)) {
                        Text(Strings.test_connection)
                    }
                }
            }
            Spacer16dpH()
            Divider()
            Spacer16dpH()
            Subtitle(Strings.base_version)
            Spacer24dpH()
            Text(
                text = Strings.version_desc.format(
                    Val.version,
                    Val.targetLifeUpCloudVersion,
                    Val.targetLifeUpAndroidVersion
                ),
                color = MaterialTheme.colors.unimportantText,
                fontSize = 14.sp
            )
            Spacer16dpH()
            Divider()
            Spacer16dpH()
            Subtitle(Strings.license)
            Spacer24dpH()
            Text(
                Strings.license_desc,
                color = MaterialTheme.colors.unimportantText,
                fontSize = 14.sp
            )
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(scrollState = scrollState)
        )
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
fun Spacer24dpH() {
    Spacer(Modifier.height(24.dp))
}


@Composable
fun Spacer16dpH() {
    Spacer(Modifier.height(16.dp))
}

@Composable
fun Spacer4dpH() {
    Spacer(Modifier.height(4.dp))
}

@Composable
fun Spacer8dpH() {
    Spacer(Modifier.height(8.dp))
}

@Composable
fun Spacer8dpWH() {
    Spacer(Modifier.size(8.dp))
}


@Preview
@Composable
fun configContentPreview() {
    ConfigScreen()
}