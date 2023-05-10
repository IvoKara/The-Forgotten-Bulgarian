package com.ivok.the_forgotten_bulgarian.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ivok.the_forgotten_bulgarian.R
import com.ivok.the_forgotten_bulgarian.databinding.ActivityFinishBinding
import com.ivok.the_forgotten_bulgarian.facades.AuthCompatActivity

class FinishActivity :
    AuthCompatActivity<ActivityFinishBinding>(R.layout.activity_finish) {
    override fun onCreate() {
        binding.buttonBack.setOnClickListener {
            startActivity(Intent(this@FinishActivity, MainActivity::class.java))
            finish()
        }
    }
}