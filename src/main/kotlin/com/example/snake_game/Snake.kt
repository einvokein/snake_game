package com.example.snake_game

import kotlin.math.sqrt
import javafx.scene.image.Image
import javafx.scene.canvas.GraphicsContext
import com.example.snake_game.SnakeGame.Companion.WINDOW_WIDTH
import com.example.snake_game.SnakeGame.Companion.WINDOW_HEIGHT

class Snake(private var length : Int, startHere : Point) {

    // list of body segments
    private var segments = mutableListOf<Point>()

    private val minLength = 5               // length of snake at the beginning
    private val segmentSize = 25            // pixel
    var currentDirection = Direction.LEFT   // direction the snake is headed at first

    // set images
    private var head = Image("head_horizontal.png")
    private val body = Image("body.png")

    // count scores
    var scores = 0

    init {
        // length cannot be less than minimal
        if(length < minLength) {
            length = minLength
        }
        // position the snake horizontally, facing left
        for (i in 0 until length) {
            segments.add(Point(startHere.x + segmentSize * i, startHere.y))
        }
    }

    private fun next(): Point {
        val lastX = segments[segments.lastIndex].x
        val lastButOneX = segments[segments.lastIndex - 1].x
        val lastY = segments[segments.lastIndex].y
        val lastButOneY = segments[segments.lastIndex - 1].y
        return if(lastX == lastButOneX) {
            Point(lastX, 2 * lastY - lastButOneY)
        } else {
            Point(2 * lastX - lastButOneX, lastY)
        }
    }

    private fun increaseLength() {
        length++
        segments.add(next())
    }

    fun move(direction : Direction) : Boolean {
        if(!isValidDirection(direction)) {
            return false
        }
        val firstX = segments[0].x
        val firstY = segments[0].y
        when(direction) {
            Direction.UP -> {
                segments.addFirst(Point(firstX, firstY - segmentSize))
                head = Image("head_vertical.png")
            }
            Direction.DOWN -> {
                segments.addFirst(Point(firstX, firstY + segmentSize))
                head = Image("head_vertical.png")
            }
            Direction.LEFT -> {
                segments.addFirst(Point(firstX - segmentSize, firstY))
                head = Image("head_horizontal.png")
            }
            Direction.RIGHT -> {
                segments.addFirst(Point(firstX + segmentSize, firstY))
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

    private fun isValidDirection(direction : Direction) : Boolean {
        return !(currentDirection == Direction.UP && direction == Direction.DOWN
                || currentDirection == Direction.LEFT && direction == Direction.RIGHT
                || currentDirection == Direction.DOWN && direction == Direction.UP
                || currentDirection == Direction.RIGHT && direction == Direction.LEFT)
    }

    fun eatFruit(fruit : Fruit) {
        if(distance(segments[0], fruit.position) <= segmentSize) {
            increaseLength()
            scores++
            fruit.generateFruit()
        }
    }

    private fun distance(point : Point, otherPoint : Point) : Double {
        return sqrt(((point.x - otherPoint.x) * (point.x - otherPoint.x)
                + (point.y - otherPoint.y) * (point.y - otherPoint.y)).toDouble())
    }

    private fun checkIfNeedsToDie() : SnakeState {
        if(segments[0].x <= -(segmentSize/2) || segments[0].x >= WINDOW_WIDTH - segmentSize/2
            || segments[0].y <= -(segmentSize/2) || segments[0].y >= WINDOW_HEIGHT - segmentSize/2) {
            return SnakeState.Dead("Hit wall")
        }
        for(i in segments.lastIndex downTo 1) {
            if(distance(segments[0], segments[i]) < segmentSize) {
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
