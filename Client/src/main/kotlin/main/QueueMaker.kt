package main

import events.*
import javafx.beans.property.DoubleProperty
import javafx.scene.paint.Color

object QueueMaker {
    lateinit var movesPerPacket : DoubleProperty
    lateinit var extraDust : DoubleProperty
    private val queue = mutableListOf<List<Double>>()
    private var curColor = Color.AZURE
    var width = 1.0

    fun setup() {
        register<MouseMove> {
            queue.add(listOf(it.x, it.y))
            if (queue.size >= movesPerPacket.value.toInt()) {
                create_packet()
                val b = queue[queue.size-1]
                queue.clear()
                queue.add(b)
            }
        }
        register<ColorChanged> {
            curColor = it.color
        }
        register<WidthChanged> {
            width = it.width
        }
        register<MouseReleased> {
            if (queue.size > 1) create_packet()
            queue.clear()
        }
    }
    fun create_packet() {
        val packet = encode_message(width, curColor, queue) + genDust(extraDust.value.toInt(), queue.size)
        emit(SendPacket(packet))
    }
    fun genDust(size: Int, count: Int): ByteArray {
        val simple = byteArrayOf(124)
        var res = byteArrayOf()
        for (i in 0 until size*count) {
            res += simple
        }
        return res
    }
}