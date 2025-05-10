package com.example.snake_game

import kotlin.math.sqrt

data class Point(var x: Int, var y: Int) {

    fun offsetX(value: Int): Point {
        return this.copy(x = this.x + value)
    }

    fun offsetY(value: Int): Point {
        return this.copy(y = this.y + value)
    }

    fun distance(otherPoint: Point): Double =
        sqrt(((x - otherPoint.x) * (x - otherPoint.x)
        + (y - otherPoint.y) * (y - otherPoint.y)).toDouble())
}

enum class Direction {UP, DOWN, LEFT, RIGHT}

sealed class SnakeState {
    data object Alive : SnakeState()
    data class Dead(val reason: String) : SnakeState()
}

class SnakeDiedException(message: String = "Snake has died") : Exception(message)