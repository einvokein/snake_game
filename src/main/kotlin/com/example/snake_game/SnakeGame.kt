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
    }

    // fx components
    private lateinit var stage: Stage
    private lateinit var menuScene: Scene
    private lateinit var menuController: MenuSceneController
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
    //private val moveIntervalNanos = 200_000_000L * (1/SPEED) // 200ms in nanoseconds
    private var moveIntervalNanos = 200_000_000.0
    //image of background
    private val background = Image("background.png", false)
    // size of background
    private val backgroundSize = BackgroundSize(
        100.0, 100.0, true, true, true, false
    )
    // logo image
    private val logo = Image("logo.png")

    // trophy image
    private val trophy =  Image("trophy.png")

    // flag indicating the game has started
    private var gameStarted = false
    // game loop for control
    private lateinit var gameLoop : AnimationTimer

    // snake
    var snake = Snake(SNAKE_LENGTH, Point(snakeStartX, snakeStartY))
    // fruits
    private var fruits = mutableListOf<Fruit>()

    override fun start(primaryStage: Stage) {
        // set stage reference
        stage = primaryStage
        //window title
        stage.title = "SnakeGame"
        stage.isResizable = false
        // set application logo
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
        // window title
        gameOverStage.title = "SnakeGame"
        gameOverStage.isResizable = false
        // set application logo
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
        // Reset game state
        gameStarted = false
        currentlyActiveKeys.clear()
        fruits.clear()
        snake = Snake(SNAKE_LENGTH, Point(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2))

        // reload fruit list
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

        moveIntervalNanos = 200_000_000.0 * (1.0/SPEED)
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
        graphicsContext.fillText("score : ${snake.scores}", 40.0, 23.0)
        graphicsContext.drawImage(trophy, 10.0, 8.0)
    }

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
            println(e.toString())
            throw RuntimeException("Snake crashed")
        }
        currentlyActiveKeys.clear()
    }
}
