package ui.text.i18n

import ui.text.StringText

class ZhHantStrings : StringText {

    override val language: String
        get() = "zh"

    override val perfer_country: Array<String>
        get() = arrayOf("TW", "HK", "MO")

    override val appName: String
        get() = "LifeUp 桌面版"

    override val module_tasks: String
        get() = "委託"
    override val module_status: String
        get() = "狀態"
    override val module_shop: String
        get() = "商店"

    override val module_achievements: String
        get() = "成就"
    override val module_achievements_short: String
        get() = "成就"

    override val module_settings: String
        get() = "設定"

    override val module_feelings: String
        get() = "感想"

    override val cancel: String
        get() = "取消"
    override val yes: String
        get() = "確定"

    override val base_config: String
        get() = "基礎"

    override val base_config_desc: String
        get() = "你需要輸入雲人升運行的 IP 地址以及端口，以便《人升-桌面版》可以讀取你的手機上的《人升》數據。IP 地址形如：192.168.1.1，你可以在《雲人升》app 上看到手機的 IP 地址和端口。端口一般為 13276。"

    override val base_version: String
        get() = "版本"

    override val version_desc: String
        get() = "桌面版本: %s\n\n適配雲人升版本: %s\n\n適配人升安卓版本: %s"

    override val ip_address: String
        get() = "Ip 地址"

    override val server_port: String
        get() = "服務端口"

    override val not_connected: String
        get() = "未連接到手機服務"

    override val connected: String
        get() = "成功連接！\n我們發現了你擁有 %d 個金幣！"

    override val test_connection: String
        get() = "測試連接"

    override val status: String
        get() = "狀態"

    override val level_display: String
        get() = "Level %d"

    override val to_next_exp_display: String
        get() = "下一級還需: %d"

    override val total_exp_display: String
        get() = "總計獲得: %d"

    override val coin: String
        get() = "金幣"

    override val oops_wip: String
        get() = "糟糕，小開發還沒做這個功能！\n（歡迎前往 Github 貢獻代碼）"

    override val btn_purchase: String
        get() = "購買"

    override val purchase_desc: String
        get() = "從桌面版購買了 %s"

    override val item_own_number: String
        get() = "擁有: %d"

    override val item_price: String
        get() = "價格: %d"

    override val snackbar_complete_task: String
        get() = "完成了任務！\n桌面版暫時不支持撤銷，如果需要請去手機上操作。"

    override val snackbar_purchase_item: String
        get() = "購買成功！"

    override val license: String
        get() = "許可"

    override val license_desc: String
        get() = "developed by Kei (LifeUp Teams)\n\n" +
                "icon designed by 下車君\n\n" +
                "using Honor HONOR Sans Font\n\n" +
                "powered by Kotlin, Jetpack Compose and Compose Desktop\n"

    override val auto_detect: String
        get() = "自動檢測"

    override val auto_detect_dialog_title: String
        get() = "自動檢測"

    override val auto_detect_dialog_empty_desc: String
        get() = "未檢測到《雲人升》服務。\n" +
                "\n" +
                "請確認你的《雲人升》處於同一局域網，並且啟動了服務。\n" +
                "\n" +
                "你可以嘗試重新開啟服務，或稍後重試。"

    override val feelings_export_group_by_day: String
        get() = "按天分組"

    override val feelings_export_group_by_month: String
        get() = "按月分組"

    override val feelings_export_group_by_year: String
        get() = "按年分組"

    override val feelings_export_dialog_title: String
        get() = "導出為 Markdown 格式？"

    override val feelings_export_dialog_desc: String
        get() = "將所有感想的內容和圖片導出為 markdown 格式。\n" +
                "\n" +
                "通過 markdown 格式，你可以輕鬆地將感想渲染成不同樣式的文檔格式，詳情可以查閱網上資料。\n" +
                "\n" +
                "請選擇導出的目的地，盡量不要選擇系統盤的系統相關文件夾，因為LifeUp可能權限不足。"

    override val feelings_export_progress_dialog_title: String
        get() = "導出中..."

    override val feelings_export_progress_dialog_desc: String
        get() = "進度: %d%"

    override val common_dir_select_title: String
        get() = "選擇文件夾"

    override val common_dir_select_button: String
        get() = "選定"

    override val common_dir_select_button_tooltip: String
        get() = "選擇當前文件夾作為保存處"

    override val about_update_button: String
        get() = "更新"
    override val about_check_updates_button: String
        get() = "檢測更新"
    override val about_message_no_update: String
        get() = "你已經擁有最新版啦~"
}