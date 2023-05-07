package com.ivok.the_forgotten_bulgarian.views

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.ivok.the_forgotten_bulgarian.R
import com.ivok.the_forgotten_bulgarian.databinding.ActivityLoginBinding
import com.ivok.the_forgotten_bulgarian.facades.AuthCompatActivity
import com.ivok.the_forgotten_bulgarian.utils.appearToast
import com.ivok.the_forgotten_bulgarian.utils.hideLoadingOverlay
import com.ivok.the_forgotten_bulgarian.utils.showLoadingOverlay


class LoginActivity : AuthCompatActivity<ActivityLoginBinding>
    (R.layout.activity_login) {

    override fun onCreate() {
        initClickableSpanText()
        initFocusListenerForEmail()
        initFocusListenerForPassword()
        setSubmitButtonListener()

        binding.loginEmail.requestFocus()
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

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener(this) { finishUserProfileCreation() }
            .addOnFailureListener(this) { exception -> checkAuthError(exception) }
    }

    private fun successfulAuthentication() {
        appearToast(this@LoginActivity, "Login successful")
        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        finish()
    }

    private fun finishUserProfileCreation() {
        val uriString = "android.resource://$packageName/${R.drawable.bulgarian_embrodery}"
        val profileUpdates = userProfileChangeRequest {
            displayName = "Hacho"
            photoUri = Uri.parse(uriString)
        }

        auth.currentUser!!.updateProfile(profileUpdates)
            .addOnSuccessListener { successfulAuthentication() }
            .addOnFailureListener {
                appearToast(this@LoginActivity, "Profile create error")
            }
            .addOnCompleteListener(this) {
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

