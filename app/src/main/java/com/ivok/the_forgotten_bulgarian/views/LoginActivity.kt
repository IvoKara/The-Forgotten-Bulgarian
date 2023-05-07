package com.ivok.the_forgotten_bulgarian.views

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Patterns
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ivok.the_forgotten_bulgarian.R
import com.ivok.the_forgotten_bulgarian.databinding.ActivityLoginBinding
import com.ivok.the_forgotten_bulgarian.utils.appearToast
import com.ivok.the_forgotten_bulgarian.utils.firebaseAuth
import com.ivok.the_forgotten_bulgarian.utils.hideLoadingOverlay
import com.ivok.the_forgotten_bulgarian.utils.showLoadingOverlay


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebase: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        firebase = Firebase.auth

        initClickableSpanText()
        initFocusListenerForEmail()
        initFocusListenerForPassword()
        setSubmitButtonListener()
    }

    private fun setSubmitButtonListener() {
        binding.run {
            buttonLoginSubmit.setOnClickListener {
                val email = loginEmail.text.toString()
                val password = loginPassword.text.toString()

                loginEmailLayout.helperText = emailError()
                loginPasswordLayout.helperText = passwordError()

                if (
                    loginEmailLayout.helperText != null ||
                    loginPasswordLayout.helperText != null
                )
                    return@setOnClickListener

                createUser(email, password)
            }
        }
    }

    private fun createUser(email: String, password: String) {
        binding.run {
            showLoadingOverlay(progressBarLogin, loadingOverlay)
        }
        firebase.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    appearToast(this@LoginActivity, "Login successful")
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    checkAuthError(it.exception!!)
                }

                binding.run {
                    hideLoadingOverlay(progressBarLogin, loadingOverlay)
                }
            }
    }

    private fun checkAuthError(exception: java.lang.Exception) {
        when (exception) {
            is FirebaseAuthUserCollisionException ->
                binding.loginEmailLayout.helperText = exception.message.toString()
            else ->
                appearToast(
                    this@LoginActivity,
                    "Authentication failed. Try again later."
                )
        }
    }

    private fun initFocusListenerForEmail() {
        binding.loginEmail.setOnFocusChangeListener { _, focused ->
            if (focused)
                return@setOnFocusChangeListener

            binding.loginEmailLayout.helperText = emailError()
        }
    }

    private fun emailError(): String? {
        val email = binding.loginEmail.text.toString()

        return if (email.isBlank()) {
            "Email cannot be empty"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            "Enter a valid email"
        } else {
            null
        }
    }

    private fun initFocusListenerForPassword() {
        binding.loginPassword.setOnFocusChangeListener { _, focused ->
            if (focused)
                return@setOnFocusChangeListener

            binding.loginPasswordLayout.helperText = passwordError()
        }
    }

    private fun passwordError(): String? {
        val password = binding.loginPassword.text.toString()

        return if (password.isBlank()) {
            "Password cannot be empty"
        } else if (password.trim().length < 8) {
            "Password must be at least 8 characters"
        } else if (!password.matches(".*[A-Za-z].*".toRegex())) {
            "Password must contain at least 1 letter"
        } else {
            null
        }
    }

    private fun initClickableSpanText() {
        val spannableString = SpannableString("Можеш да се регистрираш за секунди.")
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
                finish()
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

