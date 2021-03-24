import java.io.BufferedReader
import java.io.FileReader
import java.io.ObjectOutputStream
import java.lang.Exception
import java.lang.Thread.sleep
import java.net.ServerSocket
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.math.ceil

fun main(args: Array<String>) {
    val brip = BufferedReader(FileReader("config.txt"))
    val p: String = brip.readLine()
    val fileLocation: Path = Paths.get(p)
    val data = Files.readAllBytes(fileLocation).toMutableList()
    val sock = ServerSocket(9988).accept()
    val out = ObjectOutputStream(sock.getOutputStream())
    out.flush()
    sleep(200)
    val muchPackets = ceil(data.size / 1000.0).toInt()
    val packets = mutableListOf<ByteArray>()
    for (i in 0 until muchPackets) {
        packets.add(data.subList(0, 1000).toByteArray())
        try {
            for (a in 0 until 1000) data.removeAt(a)
        } catch (e: Exception) {
            println("Last packet id going!")
        }
    }
    out.writeObject(fileLocation.fileName.toString())
    out.writeObject(muchPackets)
    packets.forEach {
        out.writeObject(it)
    }
}