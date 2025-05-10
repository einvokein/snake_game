package com.example.snake_game

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.stage.Stage
import javafx.scene.control.Label
import javafx.scene.Node

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
        // close the game over window
        val currentStage = (event.source as Node).scene.window as Stage
        currentStage.close()
        // open menu window
        snakeGame.showMenu()
    }
}