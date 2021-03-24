package main
//
//import events.Graphics
//import events.MouseMove
//import events.register
//import gui.MainApp
//import javafx.scene.canvas.GraphicsContext
//import kotlinx.coroutines.GlobalScope
//import kotlinx.coroutines.launch
//import kotlinx.serialization.decodeFromString
//import kotlinx.serialization.encodeToString
//import kotlinx.serialization.json.Json
//import tornadofx.launch
//import java.io.BufferedReader
//import java.io.InputStreamReader
//import java.io.PrintWriter
//import java.net.Socket
//import java.util.*
//
//
//fun main() {
//    GlobalScope.launch {
//        tMain()
//    }
//    launch<MainApp>()
//}
//
//lateinit var sockMoves : Socket
//lateinit var sout : PrintWriter
//lateinit var sin : BufferedReader
//
//fun tMain() {
////    val sockData = Socket("127.0.0.1", 9997)
//    sockMoves = Socket("127.0.0.1", 9988)
//    sout = PrintWriter(sockMoves.getOutputStream(), true)
//    sin = BufferedReader(InputStreamReader(sockMoves.getInputStream()))
//
//    var g2d : GraphicsContext
//    register<Graphics> {
//        g2d = it.g2d
//    }
//    register<MouseMove> {
//        println("Move event catched")
//        sendMove(listOf(it.x, it.y))
//    }
//    GlobalScope.launch {
//        receiveMoves()
//    }
//    //Creating the mouse event handler
//
//    //Creating the mouse event handler
//}
//
//fun sendMove(move: List<Double>) {
//    println("Sending move")
//    sout.println(Json.encodeToString(move))
//    println("Move sended")
//}
//
//fun receiveMoves() {
//    var ws = ""
//    while(true) {
//        val data = sin.readLine()
//        println("New: ${data}")
//        ws += data
//        if (ws.contains("{") && ws.contains("}")) {
//            val toParse = ws.substring(ws.indexOf("{"), ws.indexOf("}"))
//            ws = ws.substring(ws.indexOf("}") + 1)
//
//            val move = Json.decodeFromString<List<Double>>(toParse)
//            print("SX: ${move[0]}, SY: ${move[1]}")
//        }
//    }
//}
//
////fun dataReading(sock: Socket) {
////    val scanner = Scanner(sock.getInputStream())
////    var wholeString = ""
////    while(scanner.hasNextLine()) {
////        wholeString += scanner.nextLine()
////        if(wholeString.contains("{") && wholeString.contains("}")) {
////            val startInd = wholeString.indexOf("{")
////            val finishInd = wholeString.indexOf("}")
////            val res = wholeString.substring(startInd, finishInd + 1)
////            wholeString = wholeString.substring(finishInd + 1, wholeString.length - 1)
////        }
////    }
////}
////
////fun movesReading(sock: Socket) {
////    val scanner = Scanner(sock.getInputStream())
////    val packet : MoverPacket = MoverPacket()
////    while (scanner.hasNextByte()) {
////        val cb = scanner.nextByte()
////        if(cb == 127.toByte()) {
////            packet.start()
////        } else if(cb == (-128).toByte()) {
////            packet.stop()
////            parseMoves(packet.data.toMutableList().toByteArray())
////        }
////    }
////}
////
////fun parseMoves(data: ByteArray): MutableList<List<Int>> {
////    return mutableListOf(listOf(2, 4))
////}
//
//

/*
fun smoother(x1: Double, x2: Double, y1: Double, y2: Double): List<List<Double>> {
        val res = mutableListOf<List<Double>>()
        res.add(listOf(x1, y1))
        var v = 9999.0
        if (x2 != x1) {
            v = (y2-y1)/(x2-x1)
        }
        var px = round(x1*100)/100
        var py = round(y1*100)/100
        var n = 0.0
        while (x1+n < x2) {
            n += 0.01
            if (round((x1+n)*100)/100 != px || round((y1 + n*v)*100)/100 != py) {
                res.add(listOf(round((x1+n)*100)/100, round((y1 + n*v)*100)/100))
                px = round((x1+n)*100)/100
                py = round((y1 + n*v)*100)/100
            }
        }
        return res
    }
 */
