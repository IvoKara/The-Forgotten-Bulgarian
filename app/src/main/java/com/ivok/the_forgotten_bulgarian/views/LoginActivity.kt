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
import android.util.Log
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.ivok.the_forgotten_bulgarian.R
import com.ivok.the_forgotten_bulgarian.databinding.ActivityLoginBinding
import com.ivok.the_forgotten_bulgarian.facades.AuthCompatActivity
import com.ivok.the_forgotten_bulgarian.extensions.appearToast
import com.ivok.the_forgotten_bulgarian.models.User
import com.ivok.the_forgotten_bulgarian.utils.hideLoadingOverlay
import com.ivok.the_forgotten_bulgarian.utils.showLoadingOverlay


class LoginActivity : AuthCompatActivity<ActivityLoginBinding>
    (R.layout.activity_login) {

    override fun onCreate() {
        initClickableSpanText()
        setLoginButtonListener()
    }

    private fun initClickableSpanText() {
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

    private fun setLoginButtonListener() {
        binding.run {
            buttonLoginSubmit.setOnClickListener {
                val email = loginEmail.text.toString()
                val password = loginPassword.text.toString()

                loginEmailLayout.helperText = emailError(email)
                loginPasswordLayout.helperText = if (password.isBlank())
                    "Password cannot be empty"
                else null

                if (
                    loginEmailLayout.helperText == null &&
                    loginPasswordLayout.helperText == null
                )
                    signInUser(email, password)
            }
        }
    }

    private fun signInUser(email: String, password: String) {
        binding.run {
            showLoadingOverlay(progressBarLogin, loadingOverlay)
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener(this) {
                database.getReference("users").child(auth.currentUser!!.displayName!!)
                    .get().addOnSuccessListener {
                        profile = it.getValue<User>()
                        successfulAuthentication()
                    }
            }
            .addOnFailureListener(this) { exception ->
                firebaseEmailError(exception)
                binding.run {
                    hideLoadingOverlay(progressBarLogin, loadingOverlay)
                }
            }
    }

    private fun successfulAuthentication() {
        appearToast(this@LoginActivity, "Login successful")
        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        finish()
    }

    private fun firebaseEmailError(exception: java.lang.Exception) {
        when (exception) {
            is FirebaseAuthInvalidUserException,
            is FirebaseAuthInvalidCredentialsException ->
                binding.loginError.text = "These credentials does not correspond to any user"
            else ->
                appearToast(
                    this,
                    "Authentication failed. Try again later."
                )
        }
    }
}

