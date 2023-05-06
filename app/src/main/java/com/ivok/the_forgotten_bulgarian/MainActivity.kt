package com.ivok.the_forgotten_bulgarian

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ivok.the_forgotten_bulgarian.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebase: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        firebase = Firebase.auth

        binding.buttonPlay.setOnClickListener {
            startActivity(Intent(this, RouteMapActivity::class.java))
        }
    }

    public override fun onStart() {
        super.onStart()
        val user = firebase.currentUser

        with(binding.buttonLogin) {
            if (user != null) {
                text = resources.getString(R.string.profile)
                setOnClickListener {
                    startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
                }
            } else {
                text = resources.getString(R.string.login)
                setOnClickListener {
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                }
            }
        }
    }
}