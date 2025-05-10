package com.example.snake_game

import javafx.scene.image.Image
import javafx.scene.canvas.GraphicsContext
import com.example.snake_game.SnakeGame.Companion.WINDOW_WIDTH
import com.example.snake_game.SnakeGame.Companion.WINDOW_HEIGHT

class Snake(length : Int, startHere : Point) {

    // list of body segments
    private var segments = mutableListOf<Point>()

    // length of the snake
    private var length = length
        set(value) {
            if(value > field){
                field = value
                segments.add(next())
            }
        }

    // minimal length of snake at the beginning
    private var minLength = 5
    // segment size in pixel
    private val segmentSize = 25
    // direction the snake is headed at first
    var currentDirection = Direction.LEFT

    // set images
    private var head = Image("head_horizontal.png")
    private val body = Image("body.png")

    // count scores
    var scores = 0

    init {
        // length cannot be less than minimal
        if(length < minLength) {
            this.length = minLength
        }
        // position the snake horizontally, facing left
        for (i in 0 until length) {
            segments.add(startHere.offsetX(segmentSize * i))
        }
    }

    private fun next(): Point {
        val last = segments[segments.lastIndex]
        val lastButOne = segments[segments.lastIndex - 1]
        return if(last.x == lastButOne.x) {
            last.offsetY(last.y - lastButOne.y)
        } else {
            last.offsetX(last.x - lastButOne.x)
        }
    }

    fun move(direction : Direction) : Boolean {
        // in case of invalid direction
        if(!isValidDirection(direction)) {
            return false
        }

        val first = segments[0]

        when(direction) {
            Direction.UP -> {
                segments.addFirst(first.offsetY(-segmentSize))
                head = Image("head_vertical.png")
            }
            Direction.DOWN -> {
                segments.addFirst(first.offsetY(segmentSize))
                head = Image("head_vertical.png")
            }
            Direction.LEFT -> {
                segments.addFirst(first.offsetX(-segmentSize))
                head = Image("head_horizontal.png")
            }
            Direction.RIGHT -> {
                segments.addFirst(first.offsetX(segmentSize))
                head = Image("head_horizontal.png")
            }
        }

        // check if the snake is dead
        checkIfNeedsToDie()
            .takeIf { it is SnakeState.Dead }
            ?.let { throw SnakeDiedException((it as SnakeState.Dead).reason) }

        segments.removeLast()
        // set direction
        currentDirection = direction
        return true
    }

    private fun isValidDirection(direction : Direction): Boolean {
        return !(currentDirection == Direction.UP && direction == Direction.DOWN
                || currentDirection == Direction.LEFT && direction == Direction.RIGHT
                || currentDirection == Direction.DOWN && direction == Direction.UP
                || currentDirection == Direction.RIGHT && direction == Direction.LEFT)
    }

    fun eatFruit(fruit : Fruit) {
        if(segments[0].distance(fruit.position) <= segmentSize) {
            length++
            scores++
            fruit.generateFruit()
        }
    }

    private fun checkIfNeedsToDie() : SnakeState {
        if(segments[0].x <= -(segmentSize/2) || segments[0].x >= WINDOW_WIDTH - segmentSize/2
            || segments[0].y <= -(segmentSize/2) || segments[0].y >= WINDOW_HEIGHT - segmentSize/2) {
            return SnakeState.Dead("Hit wall")
        }
        for(i in segments.lastIndex downTo 1) {
            if(segments[0].distance(segments[i]) < segmentSize) {
                return SnakeState.Dead("Hit itself")
            }
        }
        return SnakeState.Alive
    }

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
