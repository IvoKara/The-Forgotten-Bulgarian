package com.ivok.the_forgotten_bulgarian.models

data class User(
    val username: String,
    val email: String,
    val photoUri: String,
    val uid: String,
    val gameState: GameState = GameState()
) {
}

data class GameState(var level: Int, var question: Int) {
    constructor() : this(1, 1) {}
}