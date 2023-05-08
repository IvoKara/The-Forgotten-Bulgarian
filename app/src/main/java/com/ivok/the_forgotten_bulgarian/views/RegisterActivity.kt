package com.ivok.the_forgotten_bulgarian.views

import android.content.Intent
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.ivok.the_forgotten_bulgarian.R
import com.ivok.the_forgotten_bulgarian.databinding.ActivityRegisterBinding
import com.ivok.the_forgotten_bulgarian.facades.AuthCompatActivity
import com.ivok.the_forgotten_bulgarian.extensions.appearToast
import com.ivok.the_forgotten_bulgarian.extensions.showSoftKeyBoard
import com.ivok.the_forgotten_bulgarian.models.User
import com.ivok.the_forgotten_bulgarian.utils.hideLoadingOverlay
import com.ivok.the_forgotten_bulgarian.utils.showLoadingOverlay
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

@Suppress("UNCHECKED_CAST")
class RegisterActivity : AuthCompatActivity<ActivityRegisterBinding>
    (R.layout.activity_register) {

    private var usernameTaken = false

    private val fields = listOf(
        "Username",
        "Email",
        "Password",
        "ConfirmPassword",
    )

    private val inputData by lazy {
        listOf("", "Layout").map { suffix ->
            fields.map {
                binding::class.java.getField("register$it$suffix").get(binding)
            }
        }
    }

    private val inputFields by lazy { inputData.first() as List<TextInputEditText> }
    private val inputLayouts by lazy { inputData.last() as List<TextInputLayout> }

    override fun onCreate() {
        setRegisterButtonListener()
        showSoftKeyBoard(binding.registerUsername)
    }

    private fun setRegisterButtonListener() {
        binding.run {
            buttonRegisterSubmit.setOnClickListener {
                usernameTaken = false

                val (username, email, password, confirmPassword) = inputFields.map {
                    it.text.toString()
                }

                registerUsernameLayout.helperText = usernameError(username)
                registerEmailLayout.helperText = emailError(email)
                registerPasswordLayout.helperText = passwordError(password)
                registerConfirmPasswordLayout.helperText =
                    confirmPasswordError(password, confirmPassword)

                if (inputLayouts.all { it.helperText == null })
                    createUser()
            }
        }
    }

    private fun createUser() {
        binding.run {
            showLoadingOverlay(progressBarLogin, loadingOverlay)
        }

        val email = binding.registerEmail.text.toString()
        val password = binding.registerPassword.text.toString()
        val username = binding.registerUsername.text.toString()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener(this) { finishUserProfileCreation() }
            .addOnFailureListener(this) { exception ->
                database.getReference("users").get().addOnSuccessListener {
                    if (it.exists() && it.hasChild(username)) {
                        usernameTakenActions()
                    }
                }.addOnCompleteListener {
                    firebaseEmailError(exception)
                    binding.run {
                        hideLoadingOverlay(progressBarLogin, loadingOverlay)
                    }
                }

            }
    }

    private fun successfulAuthentication() {
        usernameTaken = false
        appearToast(this@RegisterActivity, "Registered successfully")
        startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
        finish()
        binding.run {
            hideLoadingOverlay(progressBarLogin, loadingOverlay)
        }
    }

    private fun finishUserProfileCreation() {
        val user = auth.currentUser!!
        val username = binding.registerUsername.text.toString()
        val uriString = "android.resource://$packageName/${R.drawable.bulgarian_embrodery}"
        val userData = User(
            email = user.email!!,
            username = username,
            uid = user.uid,
            photoUri = Uri.parse(uriString).toString()
        )

        val databaseEntry = database.getReference("users").child(username)
        databaseEntry.setValue(userData)
            .addOnSuccessListener {
                val profileUpdates = userProfileChangeRequest {
                    displayName = username
                    photoUri = Uri.parse(uriString)
                }
                user.updateProfile(profileUpdates)
                    .addOnSuccessListener { successfulAuthentication() }
                    .addOnFailureListener {
                        appearToast(this@RegisterActivity, "Profile create error")
                    }
            }
            .addOnFailureListener {
                usernameTakenActions()
                showSoftKeyBoard(binding.registerUsername)

                user.delete()
                auth.signOut()

                binding.run {
                    hideLoadingOverlay(progressBarLogin, loadingOverlay)
                }
            }
    }

    private fun firebaseEmailError(exception: java.lang.Exception) {
        when (exception) {
            is FirebaseAuthUserCollisionException ->
                binding.registerEmailLayout.helperText = exception.message.toString()
            else ->
                appearToast(
                    this,
                    "Authentication failed. Try again later."
                )
        }
    }

    private fun usernameTakenActions() {
        usernameTaken = true
        binding.registerUsernameLayout.helperText = "Username already taken"
    }
}