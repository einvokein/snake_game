package com.example.snake_game

import javafx.scene.image.Image
import javafx.scene.canvas.GraphicsContext
import com.example.snake_game.SnakeGame.Companion.WINDOW_WIDTH
import com.example.snake_game.SnakeGame.Companion.WINDOW_HEIGHT

class Snake(length : Int, startHere : Point) {

    // List of body segments
    private var segments = mutableListOf<Point>()

    // Length of the snake
    private var length = length
        set(value) {
            if(value > field){
                field = value
                segments.add(next())
            }
        }

    // Static attributes
    companion object {
        private val head_horizontal by lazy { Image("head_horizontal.png") }
        private val head_vertical by lazy { Image("head_vertical.png") }
        private val body by lazy { Image("body.png") }
        private const val MIN_LENGTH = 5
        private const val SEGMENT_SIZE = 25
    }

    // Set the snake facing horizontal
    private var head = head_horizontal
    // Direction the snake is headed at first
    var currentDirection = Direction.LEFT
    // count scores
    var scores = 0

    init {
        // Length cannot be less than minimal
        if(length < MIN_LENGTH) {
            this.length = MIN_LENGTH
        }
        // Position the snake horizontally, facing left
        for (i in 0 until length) {
            segments.add(startHere.offsetX(SEGMENT_SIZE * i))
        }
    }

    // Determines the position of a newly added segment
    private fun next(): Point {
        val last = segments[segments.lastIndex]
        val lastButOne = segments[segments.lastIndex - 1]
        return if(last.x == lastButOne.x) {
            last.offsetY(last.y - lastButOne.y)
        } else {
            last.offsetX(last.x - lastButOne.x)
        }
    }

    // Moves the snake
    fun move(direction: Direction): Boolean {
        // In case of invalid direction
        if(!isValidDirection(direction)) {
            return false
        }

        val first = segments[0]

        when(direction) {
            Direction.UP -> {
                segments.addFirst(first.offsetY(-SEGMENT_SIZE))
                head = head_vertical
            }
            Direction.DOWN -> {
                segments.addFirst(first.offsetY(SEGMENT_SIZE))
                head = head_vertical
            }
            Direction.LEFT -> {
                segments.addFirst(first.offsetX(-SEGMENT_SIZE))
                head = head_horizontal
            }
            Direction.RIGHT -> {
                segments.addFirst(first.offsetX(SEGMENT_SIZE))
                head = head_horizontal
            }
        }

        // Check if the snake is dead
        checkIfNeedsToDie()
            .takeIf { it is SnakeState.Dead }
            ?.let { throw SnakeDiedException((it as SnakeState.Dead).reason) }

        segments.removeLast()
        currentDirection = direction
        return true
    }

    // Checks if the given direction is valid
    // (A direction is invalid, if the snake had to make a 180-degree turn)
    private fun isValidDirection(direction: Direction): Boolean {
        return !(currentDirection == Direction.UP && direction == Direction.DOWN
                || currentDirection == Direction.LEFT && direction == Direction.RIGHT
                || currentDirection == Direction.DOWN && direction == Direction.UP
                || currentDirection == Direction.RIGHT && direction == Direction.LEFT)
    }

    // Tries eating the given fruit
    // (Administrates successful eating)
    fun eatFruit(fruit: Fruit) {
        if(segments[0].distance(fruit.position) <= SEGMENT_SIZE) {
            length++
            scores++
            fruit.generateFruit()
        }
    }

    // Method for checking game over conditions
    private fun checkIfNeedsToDie(): SnakeState {
        if(segments[0].x <= -(SEGMENT_SIZE/2) || segments[0].x >= WINDOW_WIDTH - SEGMENT_SIZE/2
            || segments[0].y <= -(SEGMENT_SIZE/2) || segments[0].y >= WINDOW_HEIGHT - SEGMENT_SIZE/2) {
            return SnakeState.Dead("The snake hit wall.")
        }
        for(i in segments.lastIndex downTo 1) {
            if(segments[0].distance(segments[i]) < SEGMENT_SIZE) {
                return SnakeState.Dead("The snake hit itself.")
            }
        }
        return SnakeState.Alive
    }

    // Draws all segments of the snake on the given graphicsContext
    fun draw(graphicsContext: GraphicsContext) {
        for(i in 0 until length) {
            if (i == 0) {
                graphicsContext.drawImage(head, segments[i].x.toDouble(), segments[i].y.toDouble())
            } else {
                graphicsContext.drawImage(body, segments[i].x.toDouble(), segments[i].y.toDouble())
            }
        }
    }
}
