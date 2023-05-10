package com.ivok.the_forgotten_bulgarian.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    val username: String? = null,
    val email: String? = null,
    val photoUri: String? = null,
    val uid: String? = null,
    var checkpoint: Checkpoint = Checkpoint()
) {
}

data class Checkpoint(var level: Int, var question: Int) {
    constructor() : this(1, 0) {}
}