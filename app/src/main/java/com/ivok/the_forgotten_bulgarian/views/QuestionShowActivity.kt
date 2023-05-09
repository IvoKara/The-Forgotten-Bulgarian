package com.ivok.the_forgotten_bulgarian.views

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.ivok.the_forgotten_bulgarian.R
import com.ivok.the_forgotten_bulgarian.databinding.ActivityQuestionShowBinding
import com.ivok.the_forgotten_bulgarian.facades.AuthCompatActivity
import com.ivok.the_forgotten_bulgarian.models.Question
import com.squareup.picasso.Picasso

class QuestionShowActivity : AuthCompatActivity<ActivityQuestionShowBinding>
    (R.layout.activity_question_show) {

    override fun onCreate() {
        val question = intent.getParcelableExtra<Question>("question")

        with(binding) {
            questionText.text = question?.text
            questionHint.text = question?.hint
            if (question?.photoUrl != null) {
                imageWrapper.visibility = View.VISIBLE
                Picasso.get().load(question.photoUrl).into(questionImage)
            } else {
                imageWrapper.visibility = View.GONE
            }
        }
    }
}