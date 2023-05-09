package com.ivok.the_forgotten_bulgarian.extensions

import com.google.android.material.textfield.TextInputLayout

fun String.titlecase() = replaceFirstChar(Char::titlecase)

fun String.untitlecase() = replaceFirstChar(Char::lowercase)

fun String.Companion.randomBgLowercase(length: Int): String {
    val allowedChars = "абвгдежзийклмнопрстуфхцчшщьъюя"
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}

fun String.hideLetters(): String {
    val spaces = " ".repeat(this.length - 2)
    return "${this.first()}${spaces}${this.last()}"
}