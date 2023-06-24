package ui.text.i18n

import ui.text.StringText

class EnStrings : StringText {

    override val language: String
        get() = "en"

    override val perfer_country: Array<String>
        get() = arrayOf("")

    override val appName: String
        get() = "LifeUp desktop"
    override val cancel: String
        get() = "Cancel"
    override val yes: String
        get() = "Yes"

    override val module_tasks: String
        get() = "Tasks"
    override val module_status: String
        get() = "Status"
    override val module_shop: String
        get() = "Shop"
    override val module_achievements_short: String
        get() = "Achi."

    override val module_achievements: String
        get() = "Achievements"
    override val module_settings: String
        get() = "Settings"

    override val module_feelings: String
        get() = "Feelings"
    override val base_config: String
        get() = "Base"

    override val base_config_desc: String
        get() = "In order for LifeUp Desktop to connect to your mobile phone data, you need to enter the IP address and port, such as xxx.xxx.xxx.xxx"

    override val base_version: String
        get() = "Version"

    override val version_desc: String
        get() = "LifeUp Desktop version: %s\n" +
                "Designed for LifeUp Cloud version: %s\nDesigned for LifeUp Android version: %s"

    override val ip_address: String
        get() = "Ip address"

    override val server_port: String
        get() = "Server port"

    override val not_connected: String
        get() = "Not connected to the server"

    override val connected: String
        get() = "Connected!\nWe found you have %d coins!"

    override val test_connection: String
        get() = "Test"

    override val status: String
        get() = "Status"

    override val level_display: String
        get() = "Level %d"

    override val to_next_exp_display: String
        get() = "To next level: %d"

    override val total_exp_display: String
        get() = "Total exp: %d"

    override val coin: String
        get() = "Coin"

    override val oops_wip: String
        get() = "Oops, this feature is still in development"

    override val btn_purchase: String
        get() = "Purchase"

    override val purchase_desc: String
        get() = "Purchase %s from LifeUp desktop"

    override val item_own_number: String
        get() = "Owned: %d"

    override val item_price: String
        get() = "Price: %d"

    override val snackbar_complete_task: String
        get() = "Task completed!\nDesktop does not support undo at the moment, if need, please go to the mobile phone to operate."

    override val snackbar_purchase_item: String
        get() = "Purchase success!"

    override val license: String
        get() = "License"

    override val license_desc: String
        get() = "developed by Kei (LifeUp Teams)\n\n" +
                "icon designed by 下车君\n\n" +
                "using Honor HONOR Sans Font\n\n" +
                "powered by Kotlin, Jetpack Compose and Compose Desktop\n"

    override val auto_detect: String
        get() = "Auto Detect"

    override val auto_detect_dialog_title: String
        get() = "Auto Detect"

    override val auto_detect_dialog_empty_desc: String
        get() = "The LifeUp Cloud service was not detected.\n" +
                "\n" +
                "Please make sure that your \"LifeUp Cloud\" is in the same local area network, and the service has been activated.\n" +
                "\n" +
                "You can try restarting the service, or try again later."

    override val feelings_export_group_by_day: String
        get() = "Group by day"

    override val feelings_export_group_by_month: String
        get() = "Group by month"

    override val feelings_export_group_by_year: String
        get() = "Group by year"

    override val feelings_export_dialog_title: String
        get() = "Export to Markdown format?"
    override val feelings_export_dialog_desc: String
        get() = "Export all feelings and pictures to markdown format.\n" +
                "\n" +
                "Through the markdown format, you can easily render your feelings into different document formats. For details, please refer to the online information.\n" +
                "\n" +
                "Please select the export destination, try not to select the system-related folder of the system disk, because LifeUp may have insufficient permissions."
    override val feelings_export_progress_dialog_title: String
        get() = "Exporting..."
    override val feelings_export_progress_dialog_desc: String
        get() = "Progress: %s%"

    override val common_dir_select_title: String
        get() = "Select a folder"

    override val common_dir_select_button: String
        get() = "Select"

    override val common_dir_select_button_tooltip: String
        get() = "Select current directory as save destination"

    override val about_update_button: String
        get() = "Update"
    override val about_check_updates_button: String
        get() = "Check for Updates"
    override val about_message_no_update: String
        get() = "No update available"
}