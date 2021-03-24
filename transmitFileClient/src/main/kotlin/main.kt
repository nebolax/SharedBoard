import java.io.File
import java.io.ObjectInputStream
import java.lang.Thread.sleep
import java.net.Socket

fun main(args: Array<String>) {
    val file = File("config.txt")
    val line = file.readLines(charset("utf-8"))[0]
    val serverIp = "tcp://" + line.substringBefore(':')
    val serverPort = line.substringAfter(':').toInt()
    val sock = Socket(serverIp, serverPort)
    sleep(100)
    println("Connected")
    val inp = ObjectInputStream(sock.getInputStream())
    val name = inp.readObject() as String
    println("Writing to $name")
    val of = File(name)
    val much = inp.readObject() as Int
    for (i in 0 until much) {
        val data = inp.readObject() as ByteArray
        of.writeBytes(data)
        println("Progress: ${(i.toDouble()/much*100)}")
    }
}