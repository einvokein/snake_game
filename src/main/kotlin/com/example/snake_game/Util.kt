package com.example.snake_game

data class Point(var x: Int, var y: Int)

enum class Direction {UP, DOWN, LEFT, RIGHT}

sealed class SnakeState {
    data object Alive : SnakeState()
    data class Dead(val reason: String) : SnakeState()
}

class SnakeDiedException(message: String = "Snake has died") : Exception(message)