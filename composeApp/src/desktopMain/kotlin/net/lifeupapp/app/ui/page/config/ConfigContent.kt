package ui.page.config

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import net.lifeupapp.app.base.Val
import service.MdnsServiceDiscovery
import ui.AppStore
import ui.ScaffoldState
import ui.Strings
import ui.page.list.Dialog
import ui.theme.unimportantText
import java.awt.Desktop
import java.net.URI

@Composable
fun ConfigScreen(modifier: Modifier = Modifier) {
    val globalStore = AppStore.current
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val model = remember { ConfigStore(coroutineScope, globalStore) }
    val state = model.state
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
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colors.error
                        )
                        Text(modifier = Modifier.padding(start = 8.dp), text = Strings.not_connected)
                    }
                    OutlinedButton(onClick = {
                        globalStore.fetchCoin()
                    }, modifier = Modifier.padding(start = 8.dp)) {
                        Text(Strings.test_connection)
                    }
                    Button(onClick = {
                        // show dialog
                        model.updateState {
                            this.copy(isDialogShowing = true)
                        }
                    }, modifier = Modifier.padding(start = 8.dp)) {
                        Text(Strings.auto_detect)
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colors.secondary
                        )
                        Text(modifier = Modifier.padding(start = 8.dp), text = Strings.connected.format(coin))
                    }
                    TextButton(onClick = {
                        globalStore.fetchCoin()
                    }, modifier = Modifier.padding(start = 8.dp)) {
                        Text(Strings.test_connection)
                    }
                    OutlinedButton(onClick = {
                        // show dialog
                        model.updateState {
                            this.copy(isDialogShowing = true)
                        }
                    }, modifier = Modifier.padding(start = 8.dp)) {
                        Text(Strings.auto_detect)
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
                    Val.AppVersion.version,
                    Val.targetLifeUpCloudVersion,
                    Val.targetLifeUpAndroidVersion
                ),
                color = MaterialTheme.colors.unimportantText,
                fontSize = 14.sp
            )
            Spacer24dpH()
            val updateInfo = globalStore.updateInfo
            if ((updateInfo?.versionCode ?: 0) > Val.versionCode) {
                // Show a button to download the update
                Button(
                    onClick = {
                        val uri = runCatching { URI(updateInfo?.downloadWebsite ?: "") }.getOrNull()
                        if (Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                            Desktop.getDesktop().browse(uri)
                        }
                    }
                ) {
                    Text(Strings.about_update_button)
                }
            } else {
                // Show a secondary button to check for updates
                val scaffoldState = ScaffoldState.current

                OutlinedButton(
                    onClick = {
                        coroutineScope.launch {
                            val result = globalStore.checkUpdateAwait()
                            if (result != null && result.versionCode <= Val.versionCode) {
                                scaffoldState.snackbarHostState.showSnackbar(Strings.about_message_no_update)
                                return@launch
                            }
                        }
                    }
                ) {
                    Text(Strings.about_check_updates_button)
                }
            }
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

    // showing the select ip dialog
    if (model.state.isDialogShowing) {
        val config = globalStore.listServerInfo()
        SelectIpDialog(config, onIpSelected = {
            globalStore.updateIpOrPort(it.ip, it.port)
            model.updateState {
                this.copy(isDialogShowing = false)
            }
        }) {
            model.updateState {
                this.copy(isDialogShowing = false)
            }
        }
    }
}

/**
 * Dialog for showing discovered ips
 */
@Composable
internal fun SelectIpDialog(
    ips: List<MdnsServiceDiscovery.IpAndPort>,
    onIpSelected: (MdnsServiceDiscovery.IpAndPort) -> Unit,
    onCloseClicked: () -> Unit
) {
    Dialog(
        title = Strings.auto_detect_dialog_title,
        onCloseRequest = onCloseClicked,
    ) {
        Column {
            if (ips.isEmpty()) {
                Text(Strings.auto_detect_dialog_empty_desc)
            }
            ips.forEach {
                TextButton(onClick = {
                    onIpSelected(it)
                }) {
                    Text(it.toString())
                }
            }
        }
    }
}


@Composable
fun Subtitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.subtitle1.copy(color = MaterialTheme.colors.primary),
        fontWeight = FontWeight.Bold,
        modifier = modifier
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
fun Spacer16dpW() {
    Spacer(Modifier.width(16.dp))
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