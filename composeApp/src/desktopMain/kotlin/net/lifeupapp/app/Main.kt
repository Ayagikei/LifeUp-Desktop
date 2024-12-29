import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import lifeupdesktop.composeapp.generated.resources.Res
import lifeupdesktop.composeapp.generated.resources.book_24px
import lifeupdesktop.composeapp.generated.resources.icon
import lifeupdesktop.composeapp.generated.resources.module_save
import net.lifeupapp.app.base.navcontroller.NavController
import net.lifeupapp.app.base.navcontroller.NavigationHost
import net.lifeupapp.app.base.navcontroller.composable
import net.lifeupapp.app.base.navcontroller.rememberNavController
import net.lifeupapp.app.ui.AppStore
import net.lifeupapp.app.ui.AppStoreImpl
import net.lifeupapp.app.ui.ScaffoldState
import net.lifeupapp.app.ui.page.Screen
import net.lifeupapp.app.ui.page.save.SaveScreen
import net.lifeupapp.app.ui.text.StringText
import net.lifeupapp.app.ui.theme.AppTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import ui.page.achievement.AchievementScreen
import ui.page.config.ConfigScreen
import ui.page.feelings.FeelingsScreen
import ui.page.item.ShopScreen
import ui.page.list.TasksScreen
import ui.page.list.VerticalScrollbar
import ui.page.list.rememberScrollbarAdapter
import ui.page.status.StatusScreen
import ui.view.fakeDialog
import java.awt.Toolkit
import java.awt.event.WindowEvent
import java.util.logging.Logger
import javax.swing.UIManager
import kotlin.system.exitProcess

@ExperimentalUnitApi
@Composable
@Preview
fun app() {
    AppTheme {
        val coroutineScope: CoroutineScope = rememberCoroutineScope()
        val globalStore = remember { AppStoreImpl(coroutineScope) }
        val scaffoldState: ScaffoldState = rememberScaffoldState()

        val navController by rememberNavController(
            startDestination =
                if (globalStore.isReadyToCall) {
                    Screen.Tasks.route
                } else {
                    Screen.Config.route
                }
        )
        var selectedItem by remember { mutableStateOf(0) }

        CompositionLocalProvider(
            AppStore provides globalStore,
            ScaffoldState provides scaffoldState,
        ) {
            val items = listOf(
                StringText.module_tasks,
                StringText.module_status,
                StringText.module_shop,
                StringText.module_achievements_short,
                StringText.module_feelings,
                stringResource(Res.string.module_save),
                StringText.module_settings
            )
            val icons = listOf(
                Icons.AutoMirrored.Filled.List,
                Icons.Filled.Person,
                Icons.Filled.ShoppingCart,
                Icons.Filled.Star,
                vectorResource(Res.drawable.book_24px),
                Icons.Filled.Info,
                Icons.Filled.Settings
            )

            Scaffold(
                scaffoldState = scaffoldState
            ) {
                Row {
                    ScrollableNavigationRail(
                        items = items,
                        icons = icons,
                        selectedItem = selectedItem,
                        navController = navController,
                        onItemSelected = { selectedItem = it }
                    )
                    Box(Modifier.fillMaxSize()) {
                        CustomNavigationHost(navController)
                    }
                }
            }

            val dialogStatus = globalStore.dialogStatus
            if (dialogStatus != null) {
                fakeDialog(dialogStatus)
            }
        }
    }
}

@Composable
fun ScrollableNavigationRail(
    items: List<String>,
    icons: List<ImageVector>,
    selectedItem: Int,
    navController: NavController,
    onItemSelected: (Int) -> Unit
) {
    val listState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxHeight()) {
        NavigationRail(
            modifier = Modifier.fillMaxHeight()
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxHeight()
            ) {
                itemsIndexed(items) { index, item ->
                    NavigationRailItem(
                        icon = { Icon(icons[index], contentDescription = item) },
                        label = { Text(item) },
                        selected = selectedItem == index,
                        onClick = {
                            onItemSelected(index)
                            when (index) {
                                0 -> navController.navigate(Screen.Tasks.route)
                                1 -> navController.navigate(Screen.Status.route)
                                2 -> navController.navigate(Screen.Shop.route)
                                3 -> navController.navigate(Screen.Achievements.route)
                                4 -> navController.navigate(Screen.Feelings.route)
                                5 -> navController.navigate(Screen.Save.route)
                                6 -> navController.navigate(Screen.Config.route)
                                else -> navController.navigate(Screen.Empty.route)
                            }
                        }
                    )
                }
            }
        }

        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterStart).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(scrollState = listState)
        )
    }
}

@Composable
fun CustomNavigationHost(
    navController: NavController
) {
    NavigationHost(navController) {
        composable(Screen.Tasks.route) {
            TasksScreen()
        }

        composable(Screen.Status.route) {
            StatusScreen()
        }

        composable(Screen.Shop.route) {
            ShopScreen()
        }

        composable(Screen.Achievements.route) {
            AchievementScreen()
        }

        composable(Screen.Feelings.route) {
            FeelingsScreen()
        }

        composable(Screen.Save.route) {
            SaveScreen()
        }

        composable(Screen.Config.route) {
            ConfigScreen()
        }

    }.build()
}


@OptIn(ExperimentalUnitApi::class, ExperimentalComposeUiApi::class)
fun main() {

    var lastError: Throwable? by mutableStateOf(null)

    application(exitProcessOnExit = false) {
        if (System.getProperty("os.name").startsWith("Windows")) {
            // To fix the window crash issue: https://github.com/JetBrains/compose-jb/issues/610
            System.setProperty("skiko.renderApi", "OPENGL")
        }
        // get native dialog UI
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

        CompositionLocalProvider(
            LocalWindowExceptionHandlerFactory provides WindowExceptionHandlerFactory { window ->
                WindowExceptionHandler {
                    lastError = it
                    window.dispatchEvent(WindowEvent(window, WindowEvent.WINDOW_CLOSING))
                    // throw it
                    throw it
                }
            }
        ) {
            // sum better window size
            val screenSize = Toolkit.getDefaultToolkit().screenSize
            val width = screenSize.width
            val height = screenSize.height
            val windowWidth = if (width > 1920) 1200 else 800
            val windowHeight = if (height > 1080) 900 else 600

            Window(
                onCloseRequest = ::exitApplication,
                title = "LifeUp",
                state = rememberWindowState(
                    position = WindowPosition(alignment = Alignment.Center),
                    size = DpSize(windowWidth.dp, windowHeight.dp)
                ),
                icon = painterResource(Res.drawable.icon)
            ) {
                app()
            }
        }
    }

    // friendly error report
    // refer to: https://github.com/JetBrains/compose-jb/issues/663 && https://github.com/JetBrains/compose-jb/issues/1764
    if (lastError != null) {
        singleWindowApplication(
            state = WindowState(width = 200.dp, height = Dp.Unspecified),
            exitProcessOnExit = false
        ) {
            Text(
                lastError?.stackTraceToString() ?: "Unknown error",
                Modifier.padding(8.dp).verticalScroll(
                    rememberScrollState()
                )
            )
        }
        exitProcess(1)
    } else {
        exitProcess(0)
    }
}

val AppScope = GlobalScope

val Any?.logger: Logger
    get() = Logger.getLogger((this?.javaClass ?: Any::class.java).toGenericString())