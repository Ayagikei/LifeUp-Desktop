package ui.page

sealed class Screen(val route: String) {
    object Tasks : Screen("tasks")

    object Config : Screen("config")

    object Empty : Screen("empty")
}