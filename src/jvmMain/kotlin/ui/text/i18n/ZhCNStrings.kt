package ui.text.i18n

import ui.text.StringText

class ZhCNStrings : StringText {

    override val language: String
        get() = "zh"
    override val appName: String
        get() = "LifeUp 桌面版"

    override val module_tasks: String
        get() = "委托"
    override val module_status: String
        get() = "状态"
    override val module_shop: String
        get() = "商店"
    override val module_achievements: String
        get() = "成就"
    override val module_settings: String
        get() = "设置"
    override val cancel: String
        get() = "取消"
    override val yes: String
        get() = "确定"

    override val base_config: String
        get() = "基础"

    override val base_config_desc: String
        get() = "你需要输入云人升运行的 IP 地址以及端口，以便《人升-桌面版》可以读取你的手机上的《人升》数据。IP 地址形如：192.168.1.1，你可以在《云人升》app 上看到手机的 IP 地址和端口。端口一般为 13276。"

    override val ip_address: String
        get() = "Ip 地址"

    override val server_port: String
        get() = "服务端口"

    override val not_connected: String
        get() = "未连接到手机服务"

    override val connected: String
        get() = "成功连接！\n我们发现了你拥有 %d 个金币！"

    override val test_connection: String
        get() = "测试连接"

    override val status: String
        get() = "状态"

    override val level_display: String
        get() = "Level %d"

    override val to_next_exp_display: String
        get() = "下一级还需: %d"

    override val total_exp_display: String
        get() = "总计获得: %d"

    override val coin: String
        get() = "金币"

    override val oops_wip: String
        get() = "糟糕，小开发还没做这个功能！\n（欢迎前往 Github 贡献代码）"
}