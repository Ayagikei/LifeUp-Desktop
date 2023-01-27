package ui.text.i18n

import ui.text.StringText

class EnStrings : StringText {

    override val language: String
        get() = "en"
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

}