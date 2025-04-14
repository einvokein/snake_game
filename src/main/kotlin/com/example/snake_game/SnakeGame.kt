package com.example.snake_game

import javafx.application.Application
import javafx.animation.AnimationTimer
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.scene.paint.Color
import javafx.scene.input.KeyCode
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image
import javafx.scene.layout.BackgroundImage
import javafx.scene.layout.*

class SnakeGame : Application() {

    companion object {
        const val WINDOW_WIDTH = 500
        const val WINDOW_HEIGHT = 500
    }

    private lateinit var mainScene: Scene
    private lateinit var graphicsContext: GraphicsContext
    private var lastFrameTime: Long = System.nanoTime()

    // use a set so duplicates are not possible
    private val currentlyActiveKeys = mutableSetOf<KeyCode>()
    private var snakeBodyX = WINDOW_WIDTH / 2
    private var snakeBodyY = WINDOW_HEIGHT / 2
    private var lastMoveTime = 0L
    private val moveIntervalNanos = 200_000_000L // 200ms in nanoseconds

    //image of background
    private val image = Image("background.png")

    private val backgroundSize = BackgroundSize(
        980.0, 980.0, true, true, true, false
    )

    // snake
    private var snake = Snake(5, Point(snakeBodyX, snakeBodyY))

    // fruit
    private var fruit = Fruit()

    override fun start(mainStage: Stage) {
        //window title
        mainStage.title = "Snake game"
        mainStage.isResizable = false

        val root = Pane()
        mainScene = Scene(root)
        mainStage.scene = mainScene

        //set canvas
        val canvas = Canvas(WINDOW_WIDTH.toDouble(), WINDOW_HEIGHT.toDouble())
        root.children.add(canvas)

        // set background
        val backgroundImage = BackgroundImage(
            image,
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.DEFAULT,
            backgroundSize
        )
        root.background = Background(backgroundImage)

        prepareActionHandlers()

        fruit.generateFruit()

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

        // draw figures
        snake.draw(graphicsContext)
        fruit.draw(graphicsContext)

        snake.eatFruit(fruit)

        // perform world updates
        // move only if enough time has passed
        if (currentNanoTime - lastMoveTime >= moveIntervalNanos) {
            updateSnakePosition()
            lastMoveTime = currentNanoTime
        }

        // display crude fps counter
        val elapsedMs = elapsedNanos / 1_000_000
        if (elapsedMs != 0L) {
            graphicsContext.fill = Color.WHITE
            graphicsContext.fillText("${1000 / elapsedMs} fps", 10.0, 10.0)
        }
    }

    private fun updateSnakePosition() {
        if (currentlyActiveKeys.contains(KeyCode.LEFT)) {
            snake.move(Direction.LEFT)
        }
        else if (currentlyActiveKeys.contains(KeyCode.RIGHT)) {
            snake.move(Direction.RIGHT)
        }
        else if (currentlyActiveKeys.contains(KeyCode.UP)) {
            snake.move(Direction.UP)
        }
        else if (currentlyActiveKeys.contains(KeyCode.DOWN)) {
            snake.move(Direction.DOWN)
        }
        else {
            snake.move(snake.currentDirection)
        }
    }

}
