package com.example.snake_game

import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image
import kotlin.math.sqrt

class Snake(private var length : Int, startHere : Point) {

    private var segments = mutableListOf<Point>()
    private val minLength = 5
    private val segmentSize = 15   //pixel
    var currentDirection = Direction.LEFT
    //snake body parts
    private var head : Image
    private var body : Image

    init {
        if(length < minLength) {
            length = minLength
        }
        for (i in 0 until length) {
            segments.add(Point(startHere.x + segmentSize * i, startHere.y))
        }
        head = Image("head.png")
        body = Image("body.png")
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

    fun move(direction : Direction) {
        if(!isValidDirection(direction)) {
            return
        }
        val firstX = segments[0].x
        val firstY = segments[0].y
        when(direction) {
            Direction.UP -> segments.addFirst(Point(firstX, firstY - segmentSize))
            Direction.DOWN -> segments.addFirst(Point(firstX, firstY + segmentSize))
            Direction.LEFT -> segments.addFirst(Point(firstX - segmentSize, firstY))
            Direction.RIGHT -> segments.addFirst(Point(firstX + segmentSize, firstY))
        }
        segments.removeLast()
        checkIfNeedsToDie()
        currentDirection = direction
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
            fruit.generateFruit()
        }
    }

    private fun distance(point : Point, otherPoint : Point) : Double {
        return sqrt(((point.x - otherPoint.x) * (point.x - otherPoint.x)
                + (point.y - otherPoint.y) * (point.y - otherPoint.y)).toDouble())
    }

    private fun checkIfNeedsToDie() {
        if(segments[0].x == 0 || segments[0].y == 0) {
            //TODO
        }
        for(i in segments.size - 1 downTo 0) {
            if(distance(segments[0], segments[i]) <= segmentSize) {
                //TODO
            }
        }
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

data class Point(var x: Int, var y: Int)

enum class Direction {UP, DOWN, LEFT, RIGHT}