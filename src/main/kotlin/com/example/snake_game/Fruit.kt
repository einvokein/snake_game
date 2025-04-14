package com.example.snake_game

import com.example.snake_game.SnakeGame.Companion.WINDOW_HEIGHT
import com.example.snake_game.SnakeGame.Companion.WINDOW_WIDTH
import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image
import kotlin.random.Random

class Fruit() {
    // position of fruit
    lateinit var position : Point
    // fruit image and size
    private val fruit = Image("fruit.png")
    private val fruitSize = 15

    fun generateFruit() {
        position = Point(Random.nextInt(fruitSize, WINDOW_WIDTH-fruitSize),
                        Random.nextInt(fruitSize, WINDOW_HEIGHT-fruitSize))
    }

    fun draw(graphicsContext: GraphicsContext) {
        graphicsContext.drawImage(fruit, position.x.toDouble(), position.y.toDouble())
    }

}