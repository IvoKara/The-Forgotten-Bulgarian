package com.ivok.the_forgotten_bulgarian.extensions

import android.content.Context
import android.graphics.PorterDuff
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.textfield.TextInputEditText
import com.ivok.the_forgotten_bulgarian.R

fun AppCompatActivity.showSoftKeyBoard(editView: TextInputEditText) {
    editView.requestFocus()
    (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        .showSoftInput(editView, InputMethodManager.SHOW_IMPLICIT)
}

fun AppCompatActivity.hideSoftKeyBoard(editView: TextInputEditText) {
    editView.clearFocus()
    (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        .hideSoftInputFromWindow(editView.windowToken, 0)
}