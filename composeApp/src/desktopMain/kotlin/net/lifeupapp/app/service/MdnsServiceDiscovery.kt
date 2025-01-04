package net.lifeupapp.app.service


/**
 * Service to discover the lifeup cloud server
 */
import net.lifeupapp.app.utils.logger
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import javax.jmdns.JmDNS
import javax.jmdns.ServiceEvent
import javax.jmdns.ServiceListener

class MdnsServiceDiscovery {

    private val log = this.logger


    data class IpAndPort(val ip: String, val port: String) {
        override fun toString(): String {
            return "$ip:$port"
        }
    }

    val ipAndPorts = HashMap<String, IpAndPort?>()
    private var jmdns: JmDNS? = null

    private val listener = object : ServiceListener {
        override fun serviceAdded(event: ServiceEvent?) {
            log.info {
                "Service added: ${event?.info}"
            }
        }

        override fun serviceRemoved(event: ServiceEvent?) {
            log.info {
                "Service removed: ${event?.info}"
            }
            event?.info?.inetAddresses?.firstOrNull()?.hostAddress?.let {
                ipAndPorts.remove(it)
            }
        }

        override fun serviceResolved(event: ServiceEvent?) {
            log.info {
                "Service resolved: ${event?.info}"
            }
            runCatching {
                if (event?.name?.contains("lifeup_cloud") == true) {
                    log.info {
                        "Service resolved, address: ${event.info.inetAddresses}"
                    }

                    val port = event.info.getPropertyString("port")
                    if (port.isNullOrEmpty()) {
                        log.info {
                            "Service resolved, but data has no port"
                        }
                        return@runCatching
                    }

                    val ip = event.info.inetAddresses.first().hostAddress
                    log.info {
                        "Service resolved, ip: $ip"
                    }
                    ipAndPorts[ip] = IpAndPort(ip, port)
                }
            }.onFailure {
                it.printStackTrace()
            }
        }
    }


    private fun logNetworkInterfaces() {
        NetworkInterface.getNetworkInterfaces().asSequence().forEach { networkInterface ->
            log.info {
                "Interface: ${networkInterface.displayName}, Name: ${networkInterface.name}"
            }
            log.info {
                "  Is up: ${networkInterface.isUp}, Is loopback: ${networkInterface.isLoopback}, Is point to point: ${networkInterface.isPointToPoint}"
            }
            networkInterface.inetAddresses.asSequence().forEach { address ->
                log.info {
                    "  Address: ${address.hostAddress}, Is IPv4: ${address is Inet4Address}"
                }
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
            log.error {
                "No suitable network interface and IPv4 address found"
            }
            return@runCatching
        }

        log.info {
            "Selected interface: ${networkInterface.displayName}, address: ${address.hostAddress}"
        }

        // Create a JmDNS instance
        jmdns = JmDNS.create(address)

        // Add a service listener
        jmdns?.addServiceListener("_lifeup._tcp.local.", listener)

        log.info {
            "JmDNS started on interface: ${networkInterface.displayName}, address: ${address.hostAddress}"
        }
    }

    fun unregister() {
        jmdns?.close()
    }
}