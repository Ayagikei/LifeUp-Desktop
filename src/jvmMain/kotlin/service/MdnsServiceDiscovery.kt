package service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import logger
import java.net.InetAddress
import java.util.logging.Level
import javax.jmdns.JmDNS
import javax.jmdns.ServiceEvent
import javax.jmdns.ServiceInfo
import javax.jmdns.ServiceListener


class MdnsServiceDiscovery {

    @Serializable
    data class MdnsInfo(
        val port: String
    )

    data class IpAndPort(val ip: String, val port: String)

    private val _ipAndPortFlow = MutableStateFlow<IpAndPort?>(null)
    val ipAndPortFlow: StateFlow<IpAndPort?> = _ipAndPortFlow

    private val listener = object : ServiceListener {
        override fun serviceAdded(event: ServiceEvent?) {
            logger.log(Level.INFO, "Service added: ${event?.info}")
        }

        override fun serviceRemoved(event: ServiceEvent?) {
            logger.log(Level.INFO, "Service removed: ${event?.info}")
        }

        override fun serviceResolved(event: ServiceEvent?) {
            logger.log(Level.INFO, "Service resolved: ${event?.info}")
            runCatching {
                if (event?.name == "lifeup_cloud") {
                    logger.log(Level.INFO, "Service resolved, address: ${event.info.inetAddresses}")
                    val data = event.info.textString
                    if (data.isEmpty()) {
                        return@runCatching
                    }
                    logger.log(Level.INFO, "Service resolved, data: $data")
                    val mdnsInfo = Json.decodeFromString<MdnsInfo>(data)
                    logger.log(Level.INFO, "Service resolved, json: $mdnsInfo")
                    val ip = event.info.inetAddresses.first().hostAddress
                    logger.log(Level.INFO, "Service resolved, ip: $ip")
                    _ipAndPortFlow.tryEmit(IpAndPort(ip, mdnsInfo.port))
                }
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    private fun onReceiveServiceInfo(serviceInfo: ServiceInfo) {
        val data = serviceInfo.textString
        if (data.isEmpty()) {
            return
        }
        logger.log(Level.INFO, "Service resolved, data: $data")
        val mdnsInfo = Json.decodeFromString<MdnsInfo>(data)
        logger.log(Level.INFO, "Service resolved, json: $mdnsInfo")
        val ip = serviceInfo.inetAddresses.first().hostAddress
        logger.log(Level.INFO, "Service resolved, ip: $ip")
        _ipAndPortFlow.tryEmit(IpAndPort(ip, mdnsInfo.port))
    }

    fun register() = runCatching {
        // Create a JmDNS instance
        val jmdns = JmDNS.create(InetAddress.getLocalHost())

        // Add a service listener
        jmdns.addServiceListener("_http._tcp.local.", listener)
    }
}