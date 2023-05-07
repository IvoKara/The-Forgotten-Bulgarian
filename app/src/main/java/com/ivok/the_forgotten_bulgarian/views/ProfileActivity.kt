package com.ivok.the_forgotten_bulgarian.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ivok.the_forgotten_bulgarian.R
import com.ivok.the_forgotten_bulgarian.databinding.ActivityProfileBinding
import com.ivok.the_forgotten_bulgarian.utils.currentUser
import com.ivok.the_forgotten_bulgarian.utils.firebaseAuth

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var firebase: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        firebase = Firebase.auth

        val user = firebase.currentUser!!

        with(binding) {
            isAnonymous.text = user.isAnonymous.toString()
            uid.text = user.uid
            email.text = user.email

            buttonSignOut.setOnClickListener {
                firebase.signOut()
                startActivity(Intent(this@ProfileActivity, MainActivity::class.java))
                finish()
            }
        }
    }
}