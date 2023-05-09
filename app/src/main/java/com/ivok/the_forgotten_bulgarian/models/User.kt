package com.ivok.the_forgotten_bulgarian.models

data class User(
    val username: String? = null,
    val email: String? = null,
    val photoUri: String? = null,
    val uid: String? = null,
    val checkpoint: Checkopoint = Checkopoint()
) {
}

data class Checkopoint(var level: Int, var question: Int) {
    constructor() : this(1, 1) {}
}