package com.ivok.the_forgotten_bulgarian.extensions

import com.google.android.material.textfield.TextInputLayout

fun String.titlecase() = replaceFirstChar(Char::titlecase)
fun String.untitlecase() = replaceFirstChar(Char::lowercase)