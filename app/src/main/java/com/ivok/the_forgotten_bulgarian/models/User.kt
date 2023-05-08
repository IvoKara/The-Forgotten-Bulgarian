package com.ivok.the_forgotten_bulgarian.models

data class User(
    val username: String,
    val email: String,
    val photoUri: String,
    val uid: String
)