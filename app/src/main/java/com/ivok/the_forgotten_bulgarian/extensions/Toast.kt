package com.ivok.the_forgotten_bulgarian.extensions

import android.app.Activity
import android.content.Context
import android.graphics.PorterDuff
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.ivok.the_forgotten_bulgarian.R

fun AppCompatActivity.appearToast(context: Context, message: String) {
    val backgroundColor = ResourcesCompat.getColor(
        context.resources,
        R.color.antique_white_400,
        null
    )

    with(Toast.makeText(context, message, Toast.LENGTH_SHORT)) {
        view?.background?.setColorFilter(backgroundColor, PorterDuff.Mode.SRC_IN)
        show()
    }
}