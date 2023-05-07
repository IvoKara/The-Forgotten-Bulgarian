package com.ivok.the_forgotten_bulgarian.utils

import android.view.View
import android.widget.ProgressBar

fun showLoadingOverlay(progressBar: ProgressBar, overlay: View) {
    progressBar.visibility = View.VISIBLE
    overlay.visibility = View.VISIBLE
}

fun hideLoadingOverlay(progressBar: ProgressBar, overlay: View) {
    progressBar.visibility = View.GONE
    overlay.visibility = View.GONE
}