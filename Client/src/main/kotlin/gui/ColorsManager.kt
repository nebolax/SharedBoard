package gui

import events.ColorChanged
import events.register
import javafx.scene.control.Button
import javafx.scene.layout.*
import javafx.scene.paint.Color

object ColorsManager {
    private val buts = mutableListOf<Button>()
    fun setup() {
        register<ColorChanged> {
            for (el in buts) {
                el.border = Border.EMPTY
            }
            buts[it.id].border =  Border(BorderStroke(
                Color.WHITE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths(4.0)
            ))
        }
    }
    fun add_button(b: Button) {
        buts.add(b)
    }
}