package ui.text

class EnStrings : StringText {
    override val appName: String
        get() = "LifeUp desktop"
    override val cancel: String
        get() = "Cancel"
    override val yes: String
        get() = "Yes"

    override val base_config: String
        get() = "Base"

    override val base_config_desc: String
        get() = "In order for LifeUp Desktop to connect to your mobile phone data, you need to enter the IP address and port, such as xxx.xxx.xxx.xxx"

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
}