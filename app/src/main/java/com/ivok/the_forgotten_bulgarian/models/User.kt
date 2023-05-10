package com.ivok.the_forgotten_bulgarian.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.util.concurrent.RecursiveTask

@IgnoreExtraProperties
data class User(
    val username: String? = null,
    val email: String? = null,
    val photoUri: String? = null,
    val uid: String? = null,
    var checkpoint: Checkpoint = Checkpoint()
) {
}

@IgnoreExtraProperties
data class Checkpoint(var level: Int, var question: Int) {
    constructor() : this(1, 1) {}

    @Exclude
    fun isValidNext(next: Checkpoint): Boolean {
        return (next.level == this.level && next.question == this.question + 1) ||
                (next.level == this.level + 1 && next.question == 1)
    }

    @Exclude
    fun isValidNext(level: Int, question: Int): Boolean {
        return this.isValidNext((Checkpoint(level, question)))
    }

    @Exclude
    fun isBefore(before: Checkpoint): Boolean {
        return (before.level == this.level && before.question < this.question) ||
                (before.level < this.level)
    }
}