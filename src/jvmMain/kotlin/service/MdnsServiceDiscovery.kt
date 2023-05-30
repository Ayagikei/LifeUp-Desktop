package service

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import logger
import java.net.InetAddress
import java.util.logging.Level
import javax.jmdns.JmDNS
import javax.jmdns.ServiceEvent
import javax.jmdns.ServiceListener

/**
 * Service to discover the lifeup cloud server
 */
class MdnsServiceDiscovery {

    @Serializable
    data class MdnsInfo(
        val port: String
    )

    data class IpAndPort(val ip: String, val port: String) {
        override fun toString(): String {
            return "$ip:$port"
        }
    }

    val ipAndPorts = HashMap<String, IpAndPort?>()

    private val listener = object : ServiceListener {
        override fun serviceAdded(event: ServiceEvent?) {
            logger.log(Level.INFO, "Service added: ${event?.info}")
        }

        override fun serviceRemoved(event: ServiceEvent?) {
            logger.log(Level.INFO, "Service removed: ${event?.info}")
            event?.info?.inetAddresses?.firstOrNull()?.hostAddress?.let {
                ipAndPorts.remove(it)
            }
        }

        override fun serviceResolved(event: ServiceEvent?) {
            logger.log(Level.INFO, "Service resolved: ${event?.info}")
            runCatching {
                if (event?.name?.contains("lifeup_cloud") == true) {
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
                    ipAndPorts[ip] = IpAndPort(ip, mdnsInfo.port)
                }
            }.onFailure {
                it.printStackTrace()
            }
        }
    }


    fun register() = runCatching {
        // Create a JmDNS instance
        val jmdns = JmDNS.create(InetAddress.getLocalHost())

        // Add a service listener
        jmdns.addServiceListener("_lifeup._tcp.local.", listener)
    }
}