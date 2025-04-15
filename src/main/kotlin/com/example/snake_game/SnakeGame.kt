package com.example.snake_game

import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.scene.paint.Color
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.event.EventHandler
import javafx.scene.input.KeyCode
import javafx.scene.image.Image
import javafx.scene.layout.BackgroundImage
import javafx.scene.layout.*
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.animation.AnimationTimer

class SnakeGame : Application() {

    companion object {
        const val WINDOW_WIDTH = 500
        const val WINDOW_HEIGHT = 500
        var SNAKE_LENGTH = 5
        var FRUIT_COUNT = 2
        var SPEED = 1.0
    }

    private lateinit var mainScene: Scene
    private lateinit var graphicsContext: GraphicsContext
    private var lastFrameTime: Long = System.nanoTime()

    // use a set so duplicates are not possible
    private val currentlyActiveKeys = mutableSetOf<KeyCode>()
    // starting position of snake
    private var snakeStartX = WINDOW_WIDTH / 2
    private var snakeStartY = WINDOW_HEIGHT / 2
    // reduce speed by moving less often
    private var lastMoveTime = 0L
    private val moveIntervalNanos = 200_000_000L * (1/SPEED) // 200ms in nanoseconds
    //image of background
    private val background = Image("background.png", false)
    // size of background
    private val backgroundSize = BackgroundSize(
        100.0, 100.0, true, true, true, false
    )
    // logo image
    private val logo = Image("logo_3.png")
    // trophy image
    private val trophy =  Image("trophy.png")
    // flag indicating the game has started
    private var gameStarted = false

    // snake
    private var snake = Snake(SNAKE_LENGTH, Point(snakeStartX, snakeStartY))

    // fruits
    private var fruits = mutableListOf<Fruit>()

    // game loop for control
    private lateinit var gameLoop : AnimationTimer

    override fun start(mainStage: Stage) {
        //window title
        mainStage.title = "Snake game"
        mainStage.isResizable = false

        // fx components
        val root = Pane()
        mainScene = Scene(root)
        mainStage.scene = mainScene

        //set canvas
        val canvas = Canvas(WINDOW_WIDTH.toDouble(), WINDOW_HEIGHT.toDouble())
        root.children.add(canvas)

        // set background
        val backgroundImage = BackgroundImage(
            background,
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.DEFAULT,
            backgroundSize
        )
        root.background = Background(backgroundImage)

        // load fruit list
        for(i in 0 until FRUIT_COUNT) {fruits.add(Fruit())}
        // generate the first fruits
        for(fruit in fruits) {
            fruit.generateFruit()
        }

        prepareActionHandlers()

        graphicsContext = canvas.graphicsContext2D

        // Main loop
        gameLoop = object : AnimationTimer() {
            override fun handle(currentNanoTime: Long) {
                tickAndRender(currentNanoTime)
            }
        }
        gameLoop.start()
        mainStage.icons.add(logo)
        mainStage.show()
    }

    private fun prepareActionHandlers() {
        mainScene.onKeyPressed = EventHandler { event ->
            currentlyActiveKeys.add(event.code)
            // starts moving the snake after pressing the first key
            gameStarted = true
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
        for(fruit in fruits) {
            fruit.draw(graphicsContext)
        }

        // snake tries to eat fruit
        for(fruit in fruits) {
            snake.eatFruit(fruit)
        }

        // perform world updates
        // move only if enough time has passed
        if (currentNanoTime - lastMoveTime >= moveIntervalNanos) {
            updateSnakePosition()
            lastMoveTime = currentNanoTime
        }

        // display scores
        val elapsedMs = elapsedNanos / 1_000_000
        if (elapsedMs != 0L) {
            graphicsContext.fill = Color.BLACK
            graphicsContext.font = Font.font("System", FontWeight.BOLD, 15.0)
            graphicsContext.fillText("scores : ${snake.scores}", 40.0, 20.0)
            graphicsContext.drawImage(trophy, 10.0, 8.0)
        }
    }

    private fun updateSnakePosition() {
        if(!gameStarted) { return }
        var successfulMove = false
        try {
            if (currentlyActiveKeys.contains(KeyCode.LEFT)) {
                successfulMove = snake.move(Direction.LEFT)
            } else if (currentlyActiveKeys.contains(KeyCode.RIGHT)) {
                successfulMove = snake.move(Direction.RIGHT)
            } else if (currentlyActiveKeys.contains(KeyCode.UP)) {
                successfulMove = snake.move(Direction.UP)
            } else if (currentlyActiveKeys.contains(KeyCode.DOWN)) {
                successfulMove = snake.move(Direction.DOWN)
            }
            if (!successfulMove) {
                snake.move(snake.currentDirection)
            }
        } catch (e: Exception) {
            gameLoop.stop()
            println(e.toString())
        }
        currentlyActiveKeys.clear()
    }

}
