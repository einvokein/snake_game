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
import javafx.scene.layout.*
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.animation.AnimationTimer
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.geometry.Pos
import javafx.stage.Modality

class SnakeGame : Application() {

    companion object {
        const val WINDOW_WIDTH = 500
        const val WINDOW_HEIGHT = 500
        var SNAKE_LENGTH = 5
        var FRUIT_COUNT = 2
        var SPEED = 1.0
    }

    // fx components
    private lateinit var stage: Stage
    private lateinit var gameScene: Scene
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
    // game loop for control
    private lateinit var gameLoop : AnimationTimer

    // snake
    private var snake = Snake(SNAKE_LENGTH, Point(snakeStartX, snakeStartY))
    // fruits
    private var fruits = mutableListOf<Fruit>()

    override fun start(primaryStage: Stage) {
        // set stage reference
        stage = primaryStage
        //window title
        stage.title = "Snake Game"
        stage.isResizable = false
        // set application logo
        stage.icons.add(logo)
        showMenu()
    }

    private fun showMenu() {
        val root = VBox(20.0)
        root.alignment = Pos.CENTER
        val button = Button("Start Game")
        button.setOnAction { startGame() }
        root.children.add(button)
        val menuScene = Scene(root, WINDOW_WIDTH.toDouble(), WINDOW_HEIGHT.toDouble())
        stage.scene = menuScene
        stage.show()
    }

    private fun showGameOver() {
        val gameOverStage = Stage()
        // window title
        gameOverStage.title = "Snake Game"
        gameOverStage.isResizable = false
        // set application logo
        gameOverStage.icons.add(logo)

        // fx components
        val root = VBox(20.0)
        root.alignment = Pos.CENTER
        val label = Label("Game Over!")
        val button = Button("Back to Menu")

        button.setOnAction {
            gameOverStage.close()
            showMenu()
        }

        root.children.addAll(label, button)
        val scene = Scene(root, 300.0, 200.0)
        gameOverStage.scene = scene
        // set the main window as owner
        gameOverStage.initOwner(stage)
        // block input to the main window
        gameOverStage.initModality(Modality.APPLICATION_MODAL)
        gameOverStage.show()
    }

    private fun startGame() {
        // Reset game state
        gameStarted = false
        currentlyActiveKeys.clear()
        fruits.clear()
        snake = Snake(SNAKE_LENGTH, Point(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2))

        // reload fruit list
        for(i in 0 until FRUIT_COUNT) {fruits.add(Fruit())}
        // generate the first fruits
        for(fruit in fruits) {
            fruit.generateFruit()
        }

        // set fx components
        val root = Pane()
        gameScene = Scene(root, WINDOW_WIDTH.toDouble(), WINDOW_HEIGHT.toDouble())

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

        graphicsContext = canvas.graphicsContext2D

        prepareActionHandlers()

        // Main loop
        gameLoop = object : AnimationTimer() {
            override fun handle(currentNanoTime: Long) {
                tickAndRender(currentNanoTime)
            }
        }
        gameLoop.start()

        // set gameScene as current scene
        stage.scene = gameScene
    }

    private fun prepareActionHandlers() {
        gameScene.onKeyPressed = EventHandler { event ->
            currentlyActiveKeys.add(event.code)
            // starts moving the snake after pressing the first key
            gameStarted = true
        }
    }

    private fun tickAndRender(currentNanoTime: Long) {
        // set lastFrameTime
        lastFrameTime = currentNanoTime

        // clear canvas
        graphicsContext.clearRect(0.0, 0.0, WINDOW_WIDTH.toDouble(), WINDOW_HEIGHT.toDouble())

        // draw figures
        snake.draw(graphicsContext)
        for (fruit in fruits) {
            fruit.draw(graphicsContext)
        }

        // snake tries to eat fruit
        for (fruit in fruits) {
            snake.eatFruit(fruit)
        }

        // perform world updates
        // move only if enough time has passed (for slower movement)
        if (currentNanoTime - lastMoveTime >= moveIntervalNanos) {
            try {
                updateSnakePosition()
            } catch (e: Exception) {
                gameLoop.stop()
                println("Game Over: ${e.message}")
                showGameOver()
            }
            lastMoveTime = currentNanoTime
        }

        // display scores
        graphicsContext.fill = Color.BLACK
        graphicsContext.font = Font.font("System", FontWeight.BOLD, 15.0)
        graphicsContext.fillText("scores : ${snake.scores}", 40.0, 23.0)
        graphicsContext.drawImage(trophy, 10.0, 8.0)
    }

    private fun updateSnakePosition() {
        // no need to update, if the game hasn't started
        if(!gameStarted) return

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
            throw RuntimeException("Snake crashed")
        }
        currentlyActiveKeys.clear()
    }
}
