package com.ivok.the_forgotten_bulgarian.facades

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

abstract class AuthCompatActivity<Binding : ViewDataBinding>
    (private val layoutId: Int) : AppCompatActivity() {
    protected lateinit var binding: Binding
    protected lateinit var firebase: FirebaseAuth

    /* clears focus from all EditText (and inherited from it) inputs */
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val focusedItem = currentFocus
            if (focusedItem is EditText) {
                val outRect = Rect()
                focusedItem.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    focusedItem.clearFocus()

                    (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                        .hideSoftInputFromWindow(focusedItem.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutId)
        firebase = Firebase.auth

        onCreate()
    }

    abstract fun onCreate()
}