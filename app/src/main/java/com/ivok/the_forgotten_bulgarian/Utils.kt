package com.ivok.the_forgotten_bulgarian

import android.content.Context
import android.graphics.PorterDuff
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat

class Toast {
    companion object {}
}

fun appearToast(context: Context, message: String) {
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

fun showLoadingOverlay(progressBar: ProgressBar, overlay: View) {
    progressBar.visibility = View.VISIBLE
    overlay.visibility = View.VISIBLE
}

fun hideLoadingOverlay(progressBar: ProgressBar, overlay: View) {
    progressBar.visibility = View.GONE
    overlay.visibility = View.GONE
}