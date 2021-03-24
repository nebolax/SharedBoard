package main

import events.*
import gui.ColorsManager
import gui.MainApp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import tornadofx.launch
import java.io.*
import java.lang.Thread.sleep
import java.net.Socket

fun main() {
    val file = File("config.txt")
    val line = file.readLines(charset("utf-8"))[0]
    val serverIp = line.substringBefore(':')
    val sererPort = line.substringAfter(':')
    val serverPort = sererPort.toInt()
    val sock = Socket(serverIp, serverPort)
    setup_logger()
    QueueMaker.setup()
    ColorsManager.setup()
    setup_sender(sock)
    GlobalScope.launch { data_receiver(sock) }
    launch<MainApp>()
}

fun setup_logger() {
    register<ReceivedDrawing> {
        val logs = FileWriter("logs.txt", true)
        println("Log ev recv")
        logs.write("New packet!\n")
        logs.write("Width: ${it.width}, r: ${it.color.red}, g: ${it.color.green}, b: ${it.color.blue}\n")
        logs.write("${it.moves}\n")
        logs.write("\n\n")
        logs.close()
    }
}

fun setup_sender(sock: Socket) {
    val dout = ObjectOutputStream(sock.getOutputStream())
    dout.flush()
    println("Ready for communications")
    register<SendPacket> {
        println("Sending message with size: ${it.data.size}")
        dout.writeObject(it.data)
    }
    register<ClearPressed> {
        dout.writeObject(byteArrayOf(127, 127, 127))
    }
    register<RequestHistory> {
        dout.writeObject(byteArrayOf(126, 126, 126))
    }
}

fun data_receiver(sock: Socket) {
    sleep(1000)
    val din = ObjectInputStream(sock.getInputStream())
    while (true) {
        val data = din.readObject() as ByteArray
        println("Got message with size: ${data.size}")
        if (data.size > 8) parse_message(data)
        else if (data.contentEquals(byteArrayOf(127, 127, 127))) {
            emit(ClearReceived())
        } else println("Got empty message")
        sleep(1)
    }
}