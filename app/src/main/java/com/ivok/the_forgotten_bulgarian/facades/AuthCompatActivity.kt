package com.ivok.the_forgotten_bulgarian.facades

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.ivok.the_forgotten_bulgarian.extensions.hideSoftKeyBoard
import com.ivok.the_forgotten_bulgarian.models.User

abstract class AuthCompatActivity<Binding : ViewDataBinding>
    (private val layoutId: Int) : AppCompatActivity() {

    companion object {
        const val databaseUrl: String =
            "https://the-forgotten-bulgarian-default-rtdb.europe-west1.firebasedatabase.app/"

        var auth: FirebaseAuth = Firebase.auth
        var database: FirebaseDatabase = Firebase.database(databaseUrl)

        var profile: User? = null
    }

    protected lateinit var binding: Binding

    /* clears focus from all EditText (and inherited from it) inputs */
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val focusedItem = currentFocus
            if (focusedItem is TextInputEditText) {
                val outRect = Rect()
                focusedItem.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    hideSoftKeyBoard(focusedItem)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutId)
        if (auth.currentUser != null && profile == null) {
            val username = auth.currentUser!!.displayName!!

            database.getReference("users").child(username)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        profile = snapshot.getValue<User>()
                        onCreate()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("Firebase Error", "Fetch user data Error", error.toException())
                    }

                })
        }

        Log.d("New User", auth.currentUser.toString())
        Log.d("New Profile", profile.toString())
        Log.d("New Database", database.toString())
        onCreate()
    }

    abstract fun onCreate()

    protected fun deleteSignedUser() {
        if (auth.currentUser != null) {
            auth.currentUser!!.delete()
            auth.signOut()
            profile = null
        }
    }

    protected fun usernameError(username: String): String? {
        return if (username.isBlank()) {
            "Username cannot be empty"
        } else if (username.trim().length < 3) {
            "Username must be at least 3 characters"
        } else if (username.trim().length > 30) {
            "Username cannot be above 30 characters"
        } else if (!Charsets.US_ASCII.newEncoder().canEncode(username)) {
            "Username should include only English letters"
        } else if (!username.matches("^\\w+$".toRegex())) {
            "Username can contain only letters, numbers and underscores"
        } else {
            null
        }
    }

    protected fun emailError(email: String): String? {
        return if (email.isBlank()) {
            "Email cannot be empty"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            "Enter a valid email address"
        } else {
            null
        }
    }

    protected fun passwordError(password: String): String? {
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

    protected fun confirmPasswordError(password: String, confirmPassword: String): String? {
        return if (confirmPassword.isBlank()) {
            "Confirmation password cannot be empty"
        } else if (confirmPassword != password) {
            "Passwords does not match"
        } else {
            null
        }
    }
}