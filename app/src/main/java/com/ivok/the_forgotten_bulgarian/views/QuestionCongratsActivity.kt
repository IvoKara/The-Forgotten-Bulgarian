package com.ivok.the_forgotten_bulgarian.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.ivok.the_forgotten_bulgarian.R
import com.ivok.the_forgotten_bulgarian.adapters.LettersListAdapter
import com.ivok.the_forgotten_bulgarian.databinding.ActivityQuestionCongratsBinding
import com.ivok.the_forgotten_bulgarian.facades.AuthCompatActivity
import com.ivok.the_forgotten_bulgarian.models.Question
import com.squareup.picasso.Picasso

class QuestionCongratsActivity :
    AuthCompatActivity<ActivityQuestionCongratsBinding>(R.layout.activity_question_congrats),
    LettersListAdapter.onLetterListener {

    override fun onCreate() {
        val question = intent.getParcelableExtra<Question>("question")

        binding.apply {
            questionAdditional.text = question?.additional
            if (question?.photoUrl != null) {
                imageWrapper.visibility = View.VISIBLE
                Picasso.get().load(question.photoUrl).into(questionImage)
            } else {
                imageWrapper.visibility = View.GONE
            }

            letters.apply {
                layoutManager = FlexboxLayoutManager(this@QuestionCongratsActivity).apply {
                    flexDirection = FlexDirection.ROW
                    justifyContent = JustifyContent.CENTER
                }
                adapter = LettersListAdapter(
                    this@QuestionCongratsActivity,
                    question!!.answer!!.toList(),
                    this@QuestionCongratsActivity
                )
            }
        }
    }

    override fun onLetterClick(letterView: View?, position: Int) {}
}