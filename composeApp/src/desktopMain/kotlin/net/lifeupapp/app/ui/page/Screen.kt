package net.lifeupapp.app.ui.page

sealed class Screen(val route: String) {
    data object Tasks : Screen("tasks")

    data object Config : Screen("config")

    data object Status : Screen("status")

    data object Shop : Screen("shop")

    data object Feelings : Screen("feelings")

    data object Achievements : Screen("achievements")

    data object Empty : Screen("empty")

    data object Save : Screen("save")
}