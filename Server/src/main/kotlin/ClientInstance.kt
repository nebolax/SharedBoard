import java.io.*
import java.lang.Thread.sleep
import java.net.Socket

class ClientInstance {
    var dout : ObjectOutputStream
    var din : ObjectInputStream
    var id = -1
    constructor(id: Int, sock: Socket) {
        this.id = id
        dout = ObjectOutputStream(sock.getOutputStream())
        dout.flush()
        sleep(1000)
        try {
            din = ObjectInputStream(sock.getInputStream())
        } catch (e: Exception) {
            din = ObjectInputStream(sock.getInputStream())
        }
    }
}