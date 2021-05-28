import events.*
import javafx.scene.paint.Color
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.FileWriter
import java.lang.Thread.sleep
import java.net.ServerSocket

fun main(args: Array<String>) {
    val port = 9998
    val servSock = ServerSocket(port)
    println("Listening on port $port")
    Mailer.setup()
    accept_new_clients(servSock)
}

object Mailer {
    val history = mutableListOf<ByteArray>()
    private val clients = mutableListOf<ClientInstance>()
    var idCounter = 0
    fun setup() {
        register<NewCLient> {
            try {
                val client = ClientInstance(idCounter++, it.sock)
                clients.add(client)
                GlobalScope.launch { client_worker(client) }
                println("Hello, ${it.sock.inetAddress.address}")
                println("History transmission succ")
            } catch(e: Exception) {
                println("Couldn't connect to new client")
            }
        }
        register<ClientDisconnected> {
            for (i in clients.indices) {
                if (clients[i].id == it.id) {
                    clients.removeAt(i)
                    break
                }
            }
            println("Minus one client, sum = ${clients.size}")
        }
    }
    fun do_mailing(data: ByteArray, senderId: Int) {
        for (cl in clients) {
            if (cl.id != senderId) {
                cl.dout.writeObject(data)
                do_logging(data, cl.id)
            }
        }
    }
    fun send_history(cl: ClientInstance) {
        for (el in history) {
            cl.dout.writeObject(el)
            do_logging(el, cl.id)
        }
        println("Successfully transmitted image")
    }
    fun do_logging(inp: ByteArray, id: Int) {
        val muchMoves = inp[0] + 128
        val width = ((inp[1] + 128)*256 + (inp[2] + 128)).toDouble() / 10.0
        val color = Color.rgb(inp[3] + 128, inp[4] + 128, inp[5] + 128)
        val moves = decode_seq_moves(inp.toList().subList(6, 6 + muchMoves*4))
        emit(MakeLog(width, color, moves))
        val logs = FileWriter("logs.txt", true)
        println("Log ev recv")
        logs.write("New packet!\n to client $id")
        logs.write("\n")
        logs.write("Width: ${width}, r: ${color.red}, g: ${color.green}, b: ${color.blue}\n")
        logs.write("${moves}\n")
        logs.write("\n\n")
        logs.close()
    }
    fun decode_seq_moves(inp: List<Byte>): List<List<Double>> {
        val res = mutableListOf<List<Double>>()
        for (i in inp.indices step 4) {
            val x = ((inp[i]+128)*256 + inp[i+1]+128).toDouble() / 10.0
            val y = ((inp[i+2]+128)*256 + inp[i+3]+128).toDouble() / 10.0
            res.add(listOf(x, y))
        }
        return res
    }
}

fun accept_new_clients(serv: ServerSocket) {
    while (true) {
        val sock = serv.accept()
        emit(New    CLient(sock))
    }
}

fun client_worker(client: ClientInstance) {
    try {
        while (true) {
            val data = client.din.readObject() as ByteArray
            if (data.contentEquals(byteArrayOf(126, 126, 126))) Mailer.send_history(client)
            else {
                if (data.contentEquals(byteArrayOf(127, 127, 127))) Mailer.history.clear()
                else Mailer.history.add(data)
                Mailer.do_mailing(data, client.id)
            }
        }
    } catch (e: Exception) {
        emit(ClientDisconnected(client.id))
    }
}