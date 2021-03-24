package main

import events.*
import javafx.scene.paint.Color
import kotlin.math.round

fun encode_message(width: Double, color: Color, moves: List<List<Double>>): ByteArray {
    val res = mutableListOf<Byte>()
    res.add((moves.size - 128).toByte())
    res.add(((width*10).toInt() / 256 - 128).toByte())
    res.add(((width*10).toInt() % 256 - 128).toByte())
    res.add((color.red * 255 - 128).toInt().toByte())
    res.add((color.green * 255 - 128).toInt().toByte())
    res.add((color.blue * 255 - 128).toInt().toByte())
    res += encode_seq_moves(moves)
    return res.toByteArray()
}

fun parse_message(inp: ByteArray) {
    val muchMoves = inp[0] + 128
    val width = ((inp[1] + 128)*256 + (inp[2] + 128)).toDouble() / 10.0
    val color = Color.rgb(inp[3] + 128, inp[4] + 128, inp[5] + 128)
    val moves = decode_seq_moves(inp.toList().subList(6, 6 + muchMoves*4))
    emit(ReceivedDrawing(width, color, moves))
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

fun encode_seq_moves(moves: List<List<Double>>): List<Byte> {
    val res = mutableListOf<Byte>()
    for (el in moves) {
        val a = (el[0]*10).toInt()
        val b = (el[1]*10).toInt()
        res += listOf((a / 256 -128).toByte(), (a % 256 -128).toByte(), (b / 256 -128).toByte(), (b % 256 -128).toByte())
    }
    return res
}

fun utf16_to_utf8(s: String): String {
    var res = ""
    for (el in s) {
        val a = el.toInt() / 256
        res += a.toChar()
        val b = el.toInt() % 256
        res += b.toChar()
    }
    return res
}

fun utf8_to_utf16(data: String): String {
    var res = ""
    var s = ""
    if (data.length % 2 == 0) s = data
    else s = data + (0).toChar()
    for (i in s.indices step 2) {
        res += (s[i].toInt()*256 + s[i+1].toInt()).toChar()
    }
    return res
}