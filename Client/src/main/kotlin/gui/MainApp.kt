package gui

import javafx.stage.Stage
import tornadofx.*

class MainApp : App(MainView::class) {
    override fun start(stage: Stage) {
        stage.isResizable = false
        super.start(stage)
    }
}