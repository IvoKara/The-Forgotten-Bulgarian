package com.ivok.the_forgotten_bulgarian.views

import android.content.Intent
import com.ivok.the_forgotten_bulgarian.R
import com.ivok.the_forgotten_bulgarian.databinding.ActivityMainBinding
import com.ivok.the_forgotten_bulgarian.facades.AuthCompatActivity

class MainActivity : AuthCompatActivity<ActivityMainBinding>
    (R.layout.activity_main) {

    override fun onCreate() {
        binding.buttonPlay.setOnClickListener {
            startActivity(Intent(this, RouteMapActivity::class.java))
        }
    }

    public override fun onStart() {
        super.onStart()
        val user = auth.currentUser

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