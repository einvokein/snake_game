package com.example.snake_game

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.stage.Stage

class SceneController {

    private lateinit var stage: Stage
    private lateinit var scene: Scene
    private lateinit var root: Parent
    @FXML
    private lateinit var welcomeText: Label

    public fun switchToMenu(actionEvent: ActionEvent) {
        val fxmlLoader = FXMLLoader(SnakeGame::class.java.getResource("menu.fxml"))
        stage = (actionEvent.source as Node).scene.window as Stage
        stage.title = "Main menu"
        scene = Scene(fxmlLoader.load())
        stage.scene = scene
        stage.show()
    }

    public fun switchToGame(actionEvent: ActionEvent) {
        val fxmlLoader = FXMLLoader(SnakeGame::class.java.getResource("game.fxml"))
        stage = (actionEvent.source as Node).scene.window as Stage
        stage.title = "Snake game"
        scene = Scene(fxmlLoader.load())
        stage.scene = scene
        stage.show()
        //START GAME HERE
    }

    @FXML
    private fun onHelloButtonClick() {
        welcomeText.text = "Welcome to JavaFX Application!"
        println("Welcome to JavaFX Application!")
    }

}