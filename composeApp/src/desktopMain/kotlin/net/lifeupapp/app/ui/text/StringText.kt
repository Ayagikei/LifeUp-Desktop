package net.lifeupapp.app.ui.text

import androidx.compose.runtime.Composable
import lifeupdesktop.composeapp.generated.resources.*
import lifeupdesktop.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

/**
 * StringText object
 *
 * This object serves as an adapter between the old custom multilingual mechanism
 * and the new Compose Multiplatform resource system.
 * https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-images-resources.html#bf65b3df
 *
 * Previously, we used a custom implementation to handle multiple languages.
 * Now, we're migrating to use the built-in localization mechanism provided by
 * Compose Multiplatform.
 *
 * This adapter allows for a smoother transition and maintains compatibility
 * with existing code that relies on the old string access pattern.
 *
 * Usage:
 * - In Composable functions, use the properties directly: StringText.someString
 * - In suspend functions or non-Composable contexts, use the suspend functions: StringText.getSomeString()
 */
object StringText {

    val appName: String
        @Composable get() = stringResource(Res.string.app_name)

    val module_tasks: String
        @Composable get() = stringResource(Res.string.module_tasks)

    val module_status: String
        @Composable get() = stringResource(Res.string.module_status)

    val module_shop: String
        @Composable get() = stringResource(Res.string.module_shop)

    val module_achievements_short: String
        @Composable get() = stringResource(Res.string.module_achievements_short)

    val module_achievements: String
        @Composable get() = stringResource(Res.string.module_achievements)

    val module_settings: String
        @Composable get() = stringResource(Res.string.module_settings)

    val module_feelings: String
        @Composable get() = stringResource(Res.string.module_feelings)

    val cancel: String
        @Composable get() = stringResource(Res.string.cancel)

    val yes: String
        @Composable get() = stringResource(Res.string.yes)

    val base_config: String
        @Composable get() = stringResource(Res.string.base_config)

    val base_config_desc: String
        @Composable get() = stringResource(Res.string.base_config_desc)

    val base_version: String
        @Composable get() = stringResource(Res.string.base_version)

    val version_desc: String
        @Composable get() = stringResource(Res.string.version_desc)

    val ip_address: String
        @Composable get() = stringResource(Res.string.ip_address)

    val server_port: String
        @Composable get() = stringResource(Res.string.server_port)

    val not_connected: String
        @Composable get() = stringResource(Res.string.not_connected)

    val connected: String
        @Composable get() = stringResource(Res.string.connected)

    val test_connection: String
        @Composable get() = stringResource(Res.string.test_connection)

    val status: String
        @Composable get() = stringResource(Res.string.status)

    val level_display: String
        @Composable get() = stringResource(Res.string.level_display)

    val to_next_exp_display: String
        @Composable get() = stringResource(Res.string.to_next_exp_display)

    val total_exp_display: String
        @Composable get() = stringResource(Res.string.total_exp_display)

    val coin: String
        @Composable get() = stringResource(Res.string.coin)

    val oops_wip: String
        @Composable get() = stringResource(Res.string.oops_wip)

    val btn_purchase: String
        @Composable get() = stringResource(Res.string.btn_purchase)

    val purchase_desc: String
        @Composable get() = stringResource(Res.string.purchase_desc)

    suspend fun getPurchaseDesc(itemName: String): String {
        return getString(Res.string.purchase_desc).format(itemName)
    }

    val item_own_number: String
        @Composable get() = stringResource(Res.string.item_own_number)

    val item_price: String
        @Composable get() = stringResource(Res.string.item_price)

    val snackbar_complete_task: String
        @Composable get() = stringResource(Res.string.snackbar_complete_task)

    val snackbar_purchase_item: String
        @Composable get() = stringResource(Res.string.snackbar_purchase_item)

    val license: String
        @Composable get() = stringResource(Res.string.license)

    val license_desc: String
        @Composable get() = stringResource(Res.string.license_desc)

    val auto_detect: String
        @Composable get() = stringResource(Res.string.auto_detect)

    val auto_detect_dialog_title: String
        @Composable get() = stringResource(Res.string.auto_detect_dialog_title)

    val auto_detect_dialog_empty_desc: String
        @Composable get() = stringResource(Res.string.auto_detect_dialog_empty_desc)

    val feelings_export_group_by_day: String
        @Composable get() = stringResource(Res.string.feelings_export_group_by_day)

    val feelings_export_group_by_month: String
        @Composable get() = stringResource(Res.string.feelings_export_group_by_month)

    val feelings_export_group_by_year: String
        @Composable get() = stringResource(Res.string.feelings_export_group_by_year)

    val feelings_export_dialog_title: String
        @Composable get() = stringResource(Res.string.feelings_export_dialog_title)

    val feelings_export_dialog_desc: String
        @Composable get() = stringResource(Res.string.feelings_export_dialog_desc)

    val feelings_export_progress_dialog_title: String
        @Composable get() = stringResource(Res.string.feelings_export_progress_dialog_title)

    val feelings_export_progress_dialog_desc: String
        @Composable get() = stringResource(Res.string.feelings_export_progress_dialog_desc)

    val common_dir_select_title: String
        @Composable get() = stringResource(Res.string.common_dir_select_title)

    val common_dir_select_button: String
        @Composable get() = stringResource(Res.string.common_dir_select_button)

    val common_dir_select_button_tooltip: String
        @Composable get() = stringResource(Res.string.common_dir_select_button_tooltip)

    val about_update_button: String
        @Composable get() = stringResource(Res.string.about_update_button)

    val about_check_updates_button: String
        @Composable get() = stringResource(Res.string.about_check_updates_button)

    val about_message_no_update: String
        @Composable get() = stringResource(Res.string.about_message_no_update)

    suspend fun getAboutMessageNoUpdate(): String {
        return getString(Res.string.about_message_no_update)
    }

    val add_tasks_title_reward: String
        @Composable get() = stringResource(Res.string.add_tasks_title_reward)

    val add_tasks_reward_coin_min: String
        @Composable get() = stringResource(Res.string.add_tasks_reward_coin_min)

    val add_tasks_reward_coin_max: String
        @Composable get() = stringResource(Res.string.add_tasks_reward_coin_max)

    val add_tasks_reward_shop_items: String
        @Composable get() = stringResource(Res.string.add_tasks_reward_shop_items)

    val common_unselected: String
        @Composable get() = stringResource(Res.string.common_unselected)

    val common_search: String
        @Composable get() = stringResource(Res.string.common_search)

    val common_unknown: String
        @Composable get() = stringResource(Res.string.common_unknown)

    val add_tasks_reward_shop_items_quantity: String
        @Composable get() = stringResource(Res.string.add_tasks_reward_shop_items_quantity)

    val add_tasks_title_skills: String
        @Composable get() = stringResource(Res.string.add_tasks_title_skills)

    val add_tasks_exp: String
        @Composable get() = stringResource(Res.string.add_tasks_exp)

    val add_tasks_title_base: String
        @Composable get() = stringResource(Res.string.add_tasks_title_base)

    val add_tasks_todo: String
        @Composable get() = stringResource(Res.string.add_tasks_todo)

    val add_tasks_notes: String
        @Composable get() = stringResource(Res.string.add_tasks_notes)

    val add_tasks_success: String
        @Composable get() = stringResource(Res.string.add_tasks_success)

    suspend fun getAddTasksSuccess(): String {
        return getString(Res.string.add_tasks_success)
    }

    val add_tasks_frequency_none: String
        @Composable get() = stringResource(Res.string.add_tasks_frequency_none)

    suspend fun getAddTasksFrequencyNone(): String {
        return getString(Res.string.add_tasks_frequency_none)
    }

    val add_tasks_frequency_daily: String
        @Composable get() = stringResource(Res.string.add_tasks_frequency_daily)

    suspend fun getAddTasksFrequencyDaily(): String {
        return getString(Res.string.add_tasks_frequency_daily)
    }

    val add_tasks_frequency_weekly: String
        @Composable get() = stringResource(Res.string.add_tasks_frequency_weekly)

    suspend fun getAddTasksFrequencyWeekly(): String {
        return getString(Res.string.add_tasks_frequency_weekly)
    }

    val add_tasks_frequency_monthly: String
        @Composable get() = stringResource(Res.string.add_tasks_frequency_monthly)

    suspend fun getAddTasksFrequencyMonthly(): String {
        return getString(Res.string.add_tasks_frequency_monthly)
    }

    val add_tasks_frequency_yearly: String
        @Composable get() = stringResource(Res.string.add_tasks_frequency_yearly)

    suspend fun getAddTasksFrequencyYearly(): String {
        return getString(Res.string.add_tasks_frequency_yearly)
    }

    val add_tasks_frequency_unlimited: String
        @Composable get() = stringResource(Res.string.add_tasks_frequency_unlimited)

    suspend fun getAddTasksFrequencyUnlimited(): String {
        return getString(Res.string.add_tasks_frequency_unlimited)
    }

    val add_tasks_failed: String
        @Composable get() = stringResource(Res.string.add_tasks_failed)

    suspend fun getAddTasksFailed(): String {
        return getString(Res.string.add_tasks_failed)
    }


    val add_tasks_dialog_title: String
        @Composable get() = stringResource(Res.string.add_tasks_dialog_title)
}