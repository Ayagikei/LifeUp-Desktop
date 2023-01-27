import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import base.navcontroller.NavController
import base.navcontroller.NavigationHost
import base.navcontroller.composable
import base.navcontroller.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import ui.AppStore
import ui.AppStoreImpl
import ui.Strings
import ui.page.Screen
import ui.page.achievement.AchievementScreen
import ui.page.config.ConfigScreen
import ui.page.feelings.FeelingsScreen
import ui.page.item.ShopScreen
import ui.page.list.TasksScreen
import ui.page.status.StatusScreen
import ui.theme.AppTheme
import ui.view.fakeDialog
import java.util.logging.Logger

@ExperimentalUnitApi
@Composable
@Preview
fun app() {
    AppTheme {

        val coroutineScope: CoroutineScope = rememberCoroutineScope()
        val globalStore = remember { AppStoreImpl(coroutineScope) }
        val scaffoldState: ScaffoldState = rememberScaffoldState()

        val navController by rememberNavController(startDestination = Screen.Tasks.route)
        var selectedItem by remember { mutableStateOf(0) }

        CompositionLocalProvider(
            AppStore provides globalStore
        ) {
            val items = listOf(
                Strings.module_tasks,
                Strings.module_status,
                Strings.module_shop,
                Strings.module_achievements_short,
                Strings.module_status,
                Strings.module_settings
            )
            val icons = listOf(
                Icons.Filled.List,
                Icons.Filled.Person,
                Icons.Filled.ShoppingCart,
                Icons.Filled.Star,
                Icons.Filled.Book,
                Icons.Filled.Settings
            )

            Scaffold(
                scaffoldState = scaffoldState
            ) {
                Row {
                    NavigationRail {
                        items.forEachIndexed { index, item ->
                            NavigationRailItem(
                                icon = { Icon(icons[index], contentDescription = item) },
                                label = { Text(item) },
                                selected = selectedItem == index,
                                onClick = {
                                    selectedItem = index
                                    when (selectedItem) {
                                        0 -> {
                                            navController.navigate(Screen.Tasks.route)
                                        }

                                        1 -> {
                                            navController.navigate(Screen.Status.route)
                                        }

                                        2 -> {
                                            navController.navigate(Screen.Shop.route)
                                        }

                                        3 -> {
                                            navController.navigate(Screen.Achievements.route)
                                        }

                                        4 -> {
                                            navController.navigate(Screen.Feelings.route)
                                        }

                                        5 -> {
                                            navController.navigate(Screen.Config.route)
                                        }

                                        else -> {
                                            navController.navigate(Screen.Empty.route)
                                        }
                                    }
                                })
                        }
                    }
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

        composable(Screen.Config.route) {
            ConfigScreen()
        }

    }.build()
}


@OptIn(ExperimentalUnitApi::class)
fun main() = application {
    // To fix the window crash issue: https://github.com/JetBrains/compose-jb/issues/610
    System.setProperty("skiko.renderApi", "OPENGL")

    Window(
        onCloseRequest = ::exitApplication,
        title = "LifeUp",
        state = rememberWindowState(
            position = WindowPosition(alignment = Alignment.Center),
            size = DpSize(1200.dp, 900.dp)
        ),
    ) {
        app()
    }
}

val AppScope = GlobalScope

val Any?.logger: Logger
    get() = Logger.getLogger((this?.javaClass ?: Any::class.java).toGenericString())