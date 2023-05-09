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
import com.ivok.the_forgotten_bulgarian.facades.AuthCompatActivity

class ProfileActivity : AuthCompatActivity<ActivityProfileBinding>
    (R.layout.activity_profile) {

    override fun onCreate() {
        val user = auth.currentUser!!

        with(binding) {
            isAnonymous.text = user.isAnonymous.toString()
            uid.text = user.uid
            email.text = user.email
            username.text = user.displayName
            userPhoto.setImageURI(user.photoUrl)

            buttonSignOut.setOnClickListener {
                signOutUser()
                startActivity(Intent(this@ProfileActivity, MainActivity::class.java))
                finish()
            }
        }
    }
}