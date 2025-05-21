package com.example.snake_game

import javafx.animation.AnimationTimer
import javafx.application.Application
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.stage.Modality
import javafx.stage.Stage

class SnakeGame : Application() {

    companion object {
        const val WINDOW_WIDTH = 500
        const val WINDOW_HEIGHT = 500
        var SNAKE_LENGTH = 5
        var FRUIT_COUNT = 2
        var SPEED = 1.0
        // Image of background
        private val background by lazy { Image("background.png", false) }
        // Logo image
        private val logo by lazy { Image("logo.png") }
        // Trophy image
        private val trophy by lazy { Image("trophy.png") }
    }

    // Fx components
    private lateinit var stage: Stage
    private lateinit var menuScene: Scene
    private lateinit var menuController: MenuSceneController
    private lateinit var gameScene: Scene
    private lateinit var graphicsContext: GraphicsContext
    private var lastFrameTime: Long = System.nanoTime()

    // Set of pressed keys
    private val currentlyActiveKeys = mutableSetOf<KeyCode>()
    // Starting position of snake
    private var snakeStartX = WINDOW_WIDTH / 2
    private var snakeStartY = WINDOW_HEIGHT / 2
    // Reduce speed by moving less often
    private var lastMoveTime = 0L
    private var moveIntervalNanos = 200_000_000.0

    // Size of background
    private val backgroundSize = BackgroundSize(
        100.0, 100.0, true, true, true, false
    )

    // Flag indicating the game has started
    private var gameStarted = false
    // Game loop for control
    private lateinit var gameLoop : AnimationTimer

    // Snake
    var snake = Snake(SNAKE_LENGTH, Point(snakeStartX, snakeStartY))
    // Fruits
    private var fruits = mutableListOf<Fruit>()

    override fun start(primaryStage: Stage) {
        stage = primaryStage
        stage.title = "SnakeGame"
        stage.isResizable = false
        stage.icons.add(logo)
        showMenu()
    }

    fun showMenu() {
        if (!::menuScene.isInitialized) {
            // load fxml file
            val loader = FXMLLoader(SnakeGame::class.java.getResource("menu.fxml"))
            val root = loader.load<Parent>()

            // set game reference of controller
            menuController = loader.getController()
            menuController.setGameApp(this)
            menuScene = Scene(root, 300.0, 400.0)
        }
        // set scene
        stage.scene = menuScene
        stage.show()
    }

    private fun showGameOver() {
        // load fxml file
        val loader = FXMLLoader(SnakeGame::class.java.getResource("game_over.fxml"))
        val root = loader.load<Parent>()

        // set game reference of controller
        val gameOverController = loader.getController<GameOverSceneController>()
        gameOverController.setGameApp(this)

        val gameOverStage = Stage()
        gameOverStage.title = "SnakeGame"
        gameOverStage.isResizable = false
        gameOverStage.icons.add(logo)

        // set scene
        val scene = Scene(root, 250.0,250.0)

        gameOverStage.scene = scene
        // set the game window as owner
        gameOverStage.initOwner(stage)
        centerNewStageOverOwner(gameOverStage, stage)
        // block input to the game window
        gameOverStage.initModality(Modality.APPLICATION_MODAL)
        gameOverStage.show()
    }

    private fun centerNewStageOverOwner(newStage: Stage, ownerStage: Stage) {
        newStage.setOnShown {
            val centerX = ownerStage.x + (ownerStage.width - newStage.width) / 2
            val centerY = ownerStage.y + (ownerStage.height - newStage.height) / 2
            newStage.x = centerX
            newStage.y = centerY
        }
    }

    fun startGame() {
        // reset game state
        gameStarted = false
        currentlyActiveKeys.clear()
        fruits.clear()
        snake = Snake(SNAKE_LENGTH, Point(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2))

        // load fruit list
        // and generate the first fruits
        for(i in 0 until FRUIT_COUNT) {fruits.add(Fruit().apply { generateFruit() })}

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
            // the snake only starts moving after pressing the first key
            gameStarted = true
        }
    }

    // Implements updates
    // by clearing canvas and redrawing objects
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

        // snake tries to eat fruits
        for (fruit in fruits) {
            snake.eatFruit(fruit)
        }

        moveIntervalNanos = 200_000_000.0 * (1.0/SPEED)
        // perform world updates
        if (currentNanoTime - lastMoveTime >= moveIntervalNanos) {
            try {
                updateSnakePosition()
            } catch (e: Exception) {
                gameLoop.stop()
                // println("Game Over: ${e.message}")
                showGameOver()
            }
            lastMoveTime = currentNanoTime
        }

        // display scores
        graphicsContext.fill = Color.BLACK
        graphicsContext.font = Font.font("System", FontWeight.BOLD, 15.0)
        graphicsContext.fillText("score : ${snake.scores}", 40.0, 23.0)
        graphicsContext.drawImage(trophy, 10.0, 8.0)
    }

    // Updates snake position
    private fun updateSnakePosition() {
        // no need to update, if the game hasn't started
        if(!gameStarted) return

        try {
            val moved = when {
                currentlyActiveKeys.contains(KeyCode.LEFT) -> snake.move(Direction.LEFT)
                currentlyActiveKeys.contains(KeyCode.RIGHT) -> snake.move(Direction.RIGHT)
                currentlyActiveKeys.contains(KeyCode.UP) -> snake.move(Direction.UP)
                currentlyActiveKeys.contains(KeyCode.DOWN) -> snake.move(Direction.DOWN)
                else -> false
            }
            if (!moved) snake.move(snake.currentDirection)
        } catch (e: Exception) {
            gameLoop.stop()
            // println(e.toString())
            throw RuntimeException("Snake crashed")
        }
        currentlyActiveKeys.clear()
    }
}
