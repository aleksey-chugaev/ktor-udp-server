import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.net.DatagramPacket
import java.net.DatagramSocket
import kotlin.io.use
import kotlin.text.String
import kotlin.text.toByteArray

fun main(args: Array<String>) {
    println("Start")

    receiveKtor()
//    receiveClassic()

    println("Exit")
}

fun receiveKtor() {
    println("receiveKtor")
    runBlocking {
        println("runBlocking")
        val selectorManager = SelectorManager(Dispatchers.IO)
        val localAddress = InetSocketAddress("0.0.0.0", 9002)
        val serverSocket = aSocket(selectorManager).udp().bind(localAddress)
        println("bind to $localAddress")
        println("waiting receive")
        val datagram = serverSocket.receive()
        val text = datagram.packet.readText()
        println("received '$text' from ${datagram.address}")

        val responsePacket = BytePacketBuilder().apply {
            writeFully("response from server".toByteArray())
        }.build()
        serverSocket.send(Datagram(address = datagram.address, packet = responsePacket))

        serverSocket.close()
    }
}

fun receiveClassic() {
    println("receiveClassic")
    DatagramSocket(9002).use { datagramSocket ->
        val datagramPacket = DatagramPacket(ByteArray(1024), 1024)
        println("before receive")
        datagramSocket.receive(datagramPacket)
        println("after receive")
        val receivedText = String(datagramPacket.data)
        println("received: $receivedText")
    }
}