package com.example.snake_game

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.stage.Stage

class GameOverSceneController {

    private lateinit var snakeGame: SnakeGame

    fun setGameApp(app: SnakeGame) {
        snakeGame = app
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