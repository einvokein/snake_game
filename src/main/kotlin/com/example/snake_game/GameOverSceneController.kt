package com.example.snake_game

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.stage.Stage
import javafx.scene.Node
import javafx.scene.control.Label

class GameOverSceneController {

    private lateinit var snakeGame: SnakeGame

    @FXML
    private lateinit var label: Label

    fun setGameApp(app: SnakeGame) {
        snakeGame = app
        label.text = "score: ${snakeGame.snake.scores}"
    }

    @FXML
    fun onRETURNButtonClick(event : ActionEvent) {
        // Close the game over window
        val currentStage = (event.source as Node).scene.window as Stage
        currentStage.close()
        // Show menu window
        snakeGame.showMenu()
    }
}