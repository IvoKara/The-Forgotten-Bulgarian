package com.ivok.the_forgotten_bulgarian

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Selection
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.ivok.the_forgotten_bulgarian.databinding.ActivityLoginBinding
import com.ivok.the_forgotten_bulgarian.databinding.ActivityMainBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        val spannableString = SpannableString("Можеш да се регистрираш за секунди.")
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }

            override fun updateDrawState(textPaint: TextPaint) {
                textPaint.color = textPaint.linkColor
                textPaint.isUnderlineText = true
            }
        }

        spannableString.setSpan(clickableSpan, 12, 23, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val smallText = binding.smallText
        smallText.movementMethod = LinkMovementMethod.getInstance()
        smallText.setText(spannableString, TextView.BufferType.SPANNABLE)
    }
}