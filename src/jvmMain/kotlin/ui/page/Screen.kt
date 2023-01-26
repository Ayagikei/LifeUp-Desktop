package ui.page

sealed class Screen(val route: String) {
    object Tasks : Screen("tasks")

    object Config : Screen("config")

    object Status : Screen("status")

    object Achievements : Screen("achievements")

    object Empty : Screen("empty")
}