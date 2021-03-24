package events

import javafx.scene.canvas.GraphicsContext
import javafx.scene.control.Button
import javafx.scene.paint.Color

class Graphics(val g2d: GraphicsContext) : Event()
class MouseMove(val x: Double, val y: Double) : Event()
class MouseReleased() : Event()
class ReceivedDrawing(val width: Double, val color: Color, val moves: List<List<Double>>) : Event()
class SendPacket(val data: ByteArray) : Event()
class ColorChanged(val color: Color, val id: Int) : Event()
class WidthChanged(val width: Double) : Event()
class ClearPressed : Event()
class ClearReceived : Event()
class RequestHistory : Event()