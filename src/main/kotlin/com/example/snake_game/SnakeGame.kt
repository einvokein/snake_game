package com.example.snake_game

import javafx.application.Application
import javafx.animation.AnimationTimer
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.scene.text.Text
import javafx.scene.paint.Color
import javafx.scene.input.KeyCode
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext


class SnakeGame : Application() {

    /*override fun start(stage: Stage) {
       stage.title = "Main menu"
       val fxmlLoader = FXMLLoader(SnakeGame::class.java.getResource("menu.fxml"))
       val scene = Scene(fxmlLoader.load())
       stage.scene = scene
       stage.show()
   }*/

    companion object {
        private const val WINDOW_WIDTH = 500
        private const val WINDOW_HEIGHT = 500
    }

    private lateinit var mainScene: Scene
    private lateinit var graphicsContext: GraphicsContext
    private var lastFrameTime: Long = System.nanoTime()

    // use a set so duplicates are not possible
    private val currentlyActiveKeys = mutableSetOf<KeyCode>()
    // snake
    private var snake = Text()

    override fun start(mainStage: Stage) {
        //window title
        mainStage.title = "Snake game"
        mainStage.isResizable = false

        val root = Group()
        mainScene = Scene(root)
        mainStage.scene = mainScene

        //set canvas
        val canvas = Canvas(WINDOW_WIDTH.toDouble(), WINDOW_HEIGHT.toDouble())
        root.children.add(canvas)

        //set snake
        snake.text = "SNAKE"
        snake.x = WINDOW_WIDTH.toDouble() / 2
        snake.y = 50.0
        root.children.add(snake)

        prepareActionHandlers()

        graphicsContext = canvas.graphicsContext2D

        // Main loop
        object : AnimationTimer() {
            override fun handle(currentNanoTime: Long) {
                tickAndRender(currentNanoTime)
            }
        }.start()

        mainStage.show()
    }

    private fun prepareActionHandlers() {
        mainScene.onKeyPressed = EventHandler { event ->
            currentlyActiveKeys.add(event.code)
        }
        mainScene.onKeyReleased = EventHandler { event ->
            currentlyActiveKeys.remove(event.code)
        }
    }

    private fun tickAndRender(currentNanoTime: Long) {
        // the time elapsed since the last frame, in nanoseconds
        // can be used for physics calculation, etc
        val elapsedNanos = currentNanoTime - lastFrameTime
        lastFrameTime = currentNanoTime

        // clear canvas
        graphicsContext.clearRect(0.0, 0.0, WINDOW_WIDTH.toDouble(), WINDOW_HEIGHT.toDouble())

        // draw background
        //graphicsContext.drawImage(space, 0.0, 0.0)

        // perform world updates
        updateSnakePosition()

        // draw sun
        //graphicsContext.drawImage(sun, sunX.toDouble(), sunY.toDouble())

        // display crude fps counter
        val elapsedMs = elapsedNanos / 1_000_000
        if (elapsedMs != 0L) {
            graphicsContext.fill = Color.WHITE
            graphicsContext.fillText("${1000 / elapsedMs} fps", 10.0, 10.0)
        }
    }

    private fun updateSnakePosition() {
        if (currentlyActiveKeys.contains(KeyCode.LEFT)) {
            //TODO
            snake.x -=5
        }
        if (currentlyActiveKeys.contains(KeyCode.RIGHT)) {
            //TODO
            snake.x +=5
        }
        if (currentlyActiveKeys.contains(KeyCode.UP)) {
            //TODO
            snake.y -=5
        }
        if (currentlyActiveKeys.contains(KeyCode.DOWN)) {
            //TODO
            snake.y +=5
        }
    }

}
