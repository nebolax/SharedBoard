package gui

import events.*
import javafx.beans.property.DoubleProperty
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.scene.canvas.GraphicsContext
import javafx.scene.input.MouseEvent
import javafx.scene.layout.*
import javafx.scene.paint.Color
import main.QueueMaker
import tornadofx.*

class MainView: View() {
    private val controller : TestController by inject()
    override val root = vbox {
        hbox {
            minHeight = 60.0
            maxWidth = 900.0
            vbox {
                label("Толщина пера") { paddingBottom = 10.0; paddingTop = 5.0 }
                slider(0.1, 5.0, 1.0, Orientation.HORIZONTAL) {
                    majorTickUnit = 1.0
                    minorTickCount = 1
                    isSnapToTicks = true
                    isShowTickMarks = true
                    isShowTickLabels = true
                    controller.penWidth = valueProperty()
                }
                paddingRight = 20.0; paddingLeft = 10.0
            }
            vbox {
                label("Толщина ластика") { paddingBottom = 10.0; paddingTop = 5.0 }
                slider(0.0, 100.0, 60.0, Orientation.HORIZONTAL) {
                    majorTickUnit = 20.0
                    minorTickCount = 1
                    isSnapToTicks = true
                    isShowTickMarks = true
                    isShowTickLabels = true
                    controller.eraserWidth = valueProperty()
                }
                paddingRight = 20.0; paddingLeft = 10.0
            }
            vbox {
                label("Количество событий в пакете") { paddingBottom = 10.0; paddingTop = 5.0 }
                slider(0.0, 100.0, 10.0, Orientation.HORIZONTAL) {
                    majorTickUnit = 40.0
                    minorTickCount = 3
                    isSnapToTicks = true
                    isShowTickMarks = true
                    isShowTickLabels = true
                    QueueMaker.movesPerPacket = valueProperty()
                }
                paddingRight = 20.0; paddingLeft = 10.0
            }
            vbox {
                label("Прибавка в каждом событии мыши (байт)") { paddingBottom = 10.0; paddingTop = 5.0 }
                slider(0.0, 500.0, 250.0, Orientation.HORIZONTAL) {
                    majorTickUnit = 200.0
                    minorTickCount = 3
                    isSnapToTicks = true
                    isShowTickMarks = true
                    isShowTickLabels = true
                    QueueMaker.extraDust = valueProperty()
                }
                paddingRight = 20.0; paddingLeft = 10.0
            }
        } // Панель со слайдерами
        hbox {
            vbox { canvas {
                    width = 850.0
                    height = 540.0
                    val g2d = graphicsContext2D
                    controller.initDraw(g2d)
                    g2d.stroke = Color.BLUE
                    emit(Graphics(g2d))
                    border = Border(
                        BorderStroke(
                            Color.BLACK,
                            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths(4.0)
                        )
                    )
                    addEventFilter(MouseEvent.MOUSE_PRESSED, controller.ph)
                    addEventFilter(MouseEvent.MOUSE_DRAGGED, controller.mh)
                    addEventFilter(MouseEvent.MOUSE_RELEASED, controller.rh)
                } }
            vbox {
                paddingTop = 4.0
                minWidth = 40.0
                maxWidth = 60.0
                for (i in 0..6) {
                    this += button {
                        val cl = controller.colors[i]
                        useMaxWidth = true
                        minHeight = 60.0
                        background = Background(BackgroundFill(cl, CornerRadii(0.0), Insets(0.0)))
                        ColorsManager.add_button(this)
                        action {
                            emit(ColorChanged(cl, i))
                        }
                    }
                }
                button("Ластик") {
                    useMaxWidth = true
                    minHeight = 60.0
                    background = Background(BackgroundFill(controller.bg, CornerRadii(0.0), Insets(0.0)))
                    ColorsManager.add_button(this)
                    action {
                        emit(ColorChanged(controller.bg, 7))
                    }
                }
                button("Очистить\nВсе") {
                    useMaxWidth = true
                    minHeight = 60.0
                    action {
                        emit(ClearPressed())
                    }
                }
            }
            emit(ColorChanged(Color.BLACK, 0))
            emit(RequestHistory())
        } // Сanvas и выбор цветов
    }
}

class TestController: Controller() {
    lateinit var g2d: GraphicsContext
    val colors = listOf<Color>(
        Color.BLACK, Color.RED, Color.GREEN, Color.BLUE,
        Color.YELLOW, Color.CYAN, Color.MAGENTA
    )
    val bg = Color.rgb(240, 240, 240)
    lateinit var penWidth: DoubleProperty
    lateinit var eraserWidth: DoubleProperty
    private var erasing = false
    private var px = 0.0; private var py = 0.0
    private var curColor = Color.BLACK
    private var locked = false

    fun initDraw(gc: GraphicsContext) {
        g2d = gc
        val canvasWidth = gc.canvas.width
        val canvasHeight = gc.canvas.height

        gc.fill = bg
        gc.stroke = Color.BLACK
        gc.fillRect(0.0, 0.0, canvasWidth, canvasHeight)
        gc.strokeRect(0.0, 0.0, canvasWidth, canvasHeight)

        gc.lineWidth = 1.0
    }
    fun clear() {
        g2d.fillRect(0.0, 0.0, g2d.canvas.width, g2d.canvas.height)
    }

    val ph: EventHandler<MouseEvent> = EventHandler {
        if (!erasing) {
            g2d.lineWidth = penWidth.value
            emit((WidthChanged(penWidth.value)))
        }
        else {
            g2d.lineWidth = eraserWidth.value
            emit(WidthChanged(eraserWidth.value))
        }
        px = it.x; py = it.y
        emit(MouseMove(it.x, it.y));
    }
    val mh: EventHandler<MouseEvent> = EventHandler {
        if(it.x >= 0.0 && it.x < g2d.canvas.width && it.y >= 0 && it.y < g2d.canvas.height) {
            while (locked) {}
            locked = true
            if (erasing) {
                g2d.lineWidth = eraserWidth.value
                draw_erase_circle(it.x, it.y, eraserWidth.value)
            } else {
                g2d.lineWidth = penWidth.value
                g2d.beginPath()
                g2d.stroke = curColor
                g2d.moveTo(px, py)
                g2d.lineTo(it.x, it.y);
                g2d.stroke();
                g2d.closePath()
            }
            px = it.x; py = it.y
            emit(MouseMove(it.x, it.y))
            locked = false
        }
    }
    val rh: EventHandler<MouseEvent> = EventHandler {
        g2d.closePath()
        if (erasing) clear_last_circle(eraserWidth.value)
        emit(MouseReleased())
    }

    private fun do_drawing(width: Double, color: Color, moves: List<List<Double>>) { //brc vt;le gfrtnfvb vfce ved gjcktlybq gjcskfnm
        while (locked) {}
        locked = true
        if (color != bg) {
            g2d.lineWidth = width
            g2d.stroke = color
            g2d.beginPath()
            g2d.moveTo(moves[0][0], moves[0][1])
            for (el in moves) {
                g2d.lineTo(el[0], el[1])
                g2d.stroke()
                g2d.moveTo(el[0], el[1])
            }
            g2d.closePath()
        } else {
            g2d.beginPath()
            for (el in moves) {
                g2d.fillOval(el[0] - width/2, el[1] - width/2, width, width)
            }
            g2d.closePath()
        }
        locked = false
    }
    private fun draw_erase_circle(x: Double, y: Double, width: Double) {
        g2d.fillOval(x - width/2, y - width/2, width, width)
        g2d.stroke = Color.BLACK
        g2d.lineWidth = 0.5
        g2d.strokeOval(x - width/2, y - width/2, width+0.5, width+0.5)
        clear_last_circle(width)
    }

    private fun clear_last_circle(width: Double) {
        g2d.lineWidth = 2.0
        g2d.stroke = bg
        g2d.strokeOval(px - width/2, py - width/2, width+0.5, width+0.5)
    }
    init {
        register<ReceivedDrawing> {
            do_drawing(it.width, it.color, it.moves)
        }
        register<ColorChanged> {
            curColor = it.color
            erasing = it.color == bg
        }
        register<ClearPressed> { clear() }
        register<ClearReceived> { clear() }
    }
}
