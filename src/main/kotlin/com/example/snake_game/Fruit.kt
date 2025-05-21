package com.example.snake_game

import kotlin.random.Random
import javafx.scene.image.Image
import javafx.scene.canvas.GraphicsContext
import com.example.snake_game.SnakeGame.Companion.WINDOW_WIDTH
import com.example.snake_game.SnakeGame.Companion.WINDOW_HEIGHT

class Fruit {

    // Position of fruit
    lateinit var position: Point

    init {
        generateFruit()
    }

    // Fruit image and size
    companion object {
        private val fruit by lazy { Image("fruit.png") }
        private const val FRUIT_SIZE = 23
    }

    // Creates a new, random position
    fun generateFruit() {
        position = Point(Random.nextInt(FRUIT_SIZE, WINDOW_WIDTH - FRUIT_SIZE),
                        Random.nextInt(FRUIT_SIZE, WINDOW_HEIGHT - FRUIT_SIZE))
    }

    // Draws image on the given graphicsContext
    fun draw(graphicsContext: GraphicsContext) {
        graphicsContext.drawImage(fruit, position.x.toDouble(), position.y.toDouble())
    }
}