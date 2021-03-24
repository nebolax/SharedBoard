package events

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import java.net.Socket

class ClientDisconnected(val id: Int) : Event()
class NewCLient(val sock: Socket) : Event()
class MakeLog(val width: Double, val color: Color, val moves: List<List<Double>>) : Event()