package com.example.snake_game

import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import java.net.URL
import java.util.*
import javafx.scene.control.ChoiceBox

class MenuSceneController : Initializable {

    private lateinit var snakeGame: SnakeGame

    @FXML
    private lateinit var lengthChoiceBox : ChoiceBox<Length>

    @FXML
    private lateinit var fruitChoiceBox : ChoiceBox<Int>

    @FXML
    private lateinit var difficultyChoiceBox : ChoiceBox<Difficulty>

    fun setGameApp(app: SnakeGame) {
        snakeGame = app
    }

    @FXML
    private fun onPLAYButtonClick() {
        setLength()
        setFruitCount()
        setSpeed()
        snakeGame.startGame()
    }

    private fun setLength() {
        SnakeGame.SNAKE_LENGTH =
            when(lengthChoiceBox.value) {
                Length.SHORT -> 5
                Length.MEDIUM -> 10
                Length.LARGE -> 15
                else -> 10
            }
    }

    private fun setFruitCount() {
        SnakeGame.FRUIT_COUNT = fruitChoiceBox.value
    }

    private fun setSpeed() {
        SnakeGame.SPEED =
            when(difficultyChoiceBox.value) {
                Difficulty.EASY -> 1.0
                Difficulty.NORMAL -> 2.0
                Difficulty.HARD -> 3.0
                else -> 1.0
            }
    }

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        lengthChoiceBox.items.addAll(Length.entries)
        lengthChoiceBox.value = Length.MEDIUM
        fruitChoiceBox.items.addAll(numOfFruit)
        fruitChoiceBox.value = 2
        difficultyChoiceBox.items.addAll(Difficulty.entries)
        difficultyChoiceBox.value = Difficulty.NORMAL
    }

    enum class Length {SHORT, MEDIUM, LARGE }

    private val numOfFruit = arrayOf(1,2,3,4,5)

    enum class Difficulty {EASY, NORMAL, HARD }
}