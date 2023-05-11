package com.ivok.the_forgotten_bulgarian.facades

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.ProxyFileDescriptorCallback
import android.util.Log
import android.util.Patterns
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.gms.tasks.Task
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
import com.ivok.the_forgotten_bulgarian.extensions.appearToast
import com.ivok.the_forgotten_bulgarian.extensions.hideSoftKeyBoard
import com.ivok.the_forgotten_bulgarian.models.Checkpoint
import com.ivok.the_forgotten_bulgarian.models.Question
import com.ivok.the_forgotten_bulgarian.models.User
import com.ivok.the_forgotten_bulgarian.views.FinishActivity

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

        Log.d("New User", auth.currentUser.toString())
        Log.d("New Database", database.toString())
        onCreate()
    }

    open fun onCreate() {}

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            val username = auth.currentUser!!.displayName!!

            database.getReference("users").child(username)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        profile = snapshot.getValue<User>()!!
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("Firebase Error", "Fetch user data Error", error.toException())
                    }

                })
            Log.d("Profile", "After Updated $profile")
        }
    }

    protected fun updateCheckpoint(checkpoint: Checkpoint): Task<Void> {
        return database.getReference("users/${profile!!.username}/checkpoint")
            .setValue(checkpoint)
    }

    protected fun updateCheckpoint(level: Int, question: Int): Task<Void> {
        return this.updateCheckpoint(Checkpoint(level, question))
    }

    private fun updateCheckpointCallback(checkpoint: Checkpoint, callback: () -> Unit = {}) {
        updateCheckpoint(checkpoint)
            .addOnSuccessListener {
                callback()
            }.addOnFailureListener {
                Log.e("Firebase", "Update Checkpoint error", it)
            }
    }

    protected fun moveUserToNextLevel(checkpoint: Checkpoint, additional: () -> Unit = {}) {
        database.getReference("quiz/levels/")
            .child("${checkpoint.level}/questions/0")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("Firebase", snapshot.getValue().toString())
                    Log.d("Firebase", "Exists Snapshot: ${snapshot.exists()}")
                    Log.d("Firebase", snapshot.getValue().toString())
                    if (snapshot.exists()) {
                        val next = Checkpoint(checkpoint.level + 1, 1)
                        updateCheckpointCallback(next) { additional() }
                    } else {
                        startActivity(Intent(this@AuthCompatActivity, FinishActivity::class.java))
                        finish()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Quiz Fetch error", error.toException())
                }
            })
    }

    protected fun moveUserToNextQuestion(current: Checkpoint, additional: () -> Unit) {
        Log.w("Firebase checkpoint", current.toString())
        database.getReference("quiz/levels/${current.level - 1}/")
            .child("questions/${current.question}")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("Firebase", snapshot.getValue().toString())
                    Log.d("Firebase", "Exists Snapshot: ${snapshot.exists()}")
                    val childPath = (current.question - 1).toString()
                    if (snapshot.exists()) {
                        val next = Checkpoint(current.level, current.question + 1)
                        updateCheckpointCallback(next) { additional() }
                    } else {
                        moveUserToNextLevel(current, additional)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Quiz Fetch error", error.toException())
                }

            })
    }

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