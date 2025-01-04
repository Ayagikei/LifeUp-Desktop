package net.lifeupapp.app.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lifeupdesktop.composeapp.generated.resources.Res
import lifeupdesktop.composeapp.generated.resources.empty_tasks
import lifeupdesktop.composeapp.generated.resources.state_error
import lifeupdesktop.composeapp.generated.resources.state_loading
import org.jetbrains.compose.resources.stringResource

sealed class PageState {
    data object Loading : PageState()
    data object Empty : PageState()
    data class Error(val message: String) : PageState()
    data object Content : PageState()
}

@Composable
fun StateLayout(
    state: PageState,
    modifier: Modifier = Modifier,
    emptyText: String = stringResource(Res.string.empty_tasks),
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            PageState.Loading -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        stringResource(Res.string.state_loading),
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                        fontSize = 14.sp
                    )
                }
            }

            PageState.Empty -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Text(
                        emptyText,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            is PageState.Error -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Text(
                        stringResource(Res.string.state_error, state.message),
                        color = MaterialTheme.colors.error,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            PageState.Content -> {
                content()
            }
        }
    }
} 