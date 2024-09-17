package service


/**
 * Service to discover the lifeup cloud server
 */
import logger
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.logging.Level
import javax.jmdns.JmDNS
import javax.jmdns.ServiceEvent
import javax.jmdns.ServiceListener

class MdnsServiceDiscovery {

    data class IpAndPort(val ip: String, val port: String) {
        override fun toString(): String {
            return "$ip:$port"
        }
    }

    val ipAndPorts = HashMap<String, IpAndPort?>()
    private var jmdns: JmDNS? = null

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

                    val port = event.info.getPropertyString("port")
                    if (port.isNullOrEmpty()) {
                        logger.log(Level.INFO, "Service resolved, but data has no port")
                        return@runCatching
                    }

                    val ip = event.info.inetAddresses.first().hostAddress
                    logger.log(Level.INFO, "Service resolved, ip: $ip")
                    ipAndPorts[ip] = IpAndPort(ip, port)
                }
            }.onFailure {
                it.printStackTrace()
            }
        }
    }


    private fun logNetworkInterfaces() {
        NetworkInterface.getNetworkInterfaces().asSequence().forEach { networkInterface ->
            logger.log(
                Level.INFO,
                "Interface: ${networkInterface.displayName}, Name: ${networkInterface.name}"
            )
            logger.log(
                Level.INFO,
                "  Is up: ${networkInterface.isUp}, Is loopback: ${networkInterface.isLoopback}, Is point to point: ${networkInterface.isPointToPoint}"
            )
            networkInterface.inetAddresses.asSequence().forEach { address ->
                logger.log(
                    Level.INFO,
                    "  Address: ${address.hostAddress}, Is IPv4: ${address is Inet4Address}"
                )
            }
        }
    }

    private fun findSuitableNetworkInterface(): Pair<NetworkInterface, InetAddress>? {
        return NetworkInterface.getNetworkInterfaces().asSequence()
            .filter { it.isUp && !it.isLoopback && !it.isPointToPoint }
            .flatMap { networkInterface ->
                networkInterface.inetAddresses.asSequence()
                    .filter { it is Inet4Address }
                    .map { Pair(networkInterface, it) }
            }
            .firstOrNull()
    }

    fun register() = runCatching {
        logNetworkInterfaces()

        val (networkInterface, address) = findSuitableNetworkInterface() ?: run {
            logger.log(Level.SEVERE, "No suitable network interface and IPv4 address found")
            return@runCatching
        }

        logger.log(
            Level.INFO,
            "Selected interface: ${networkInterface.displayName}, address: ${address.hostAddress}"
        )

        // Create a JmDNS instance
        jmdns = JmDNS.create(address)

        // Add a service listener
        jmdns?.addServiceListener("_lifeup._tcp.local.", listener)

        logger.log(
            Level.INFO,
            "JmDNS started on interface: ${networkInterface.displayName}, address: ${address.hostAddress}"
        )
    }

    fun unregister() {
        jmdns?.close()
    }
}