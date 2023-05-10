package com.ivok.the_forgotten_bulgarian.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.ivok.the_forgotten_bulgarian.R
import com.ivok.the_forgotten_bulgarian.adapters.LettersListAdapter
import com.ivok.the_forgotten_bulgarian.adapters.QuestionsListAdapter
import com.ivok.the_forgotten_bulgarian.databinding.ActivityQuestionCongratsBinding
import com.ivok.the_forgotten_bulgarian.extensions.appearToast
import com.ivok.the_forgotten_bulgarian.facades.AuthCompatActivity
import com.ivok.the_forgotten_bulgarian.models.Level
import com.ivok.the_forgotten_bulgarian.models.Question
import com.squareup.picasso.Picasso

class QuestionCongratsActivity :
    AuthCompatActivity<ActivityQuestionCongratsBinding>(R.layout.activity_question_congrats) {

    override fun onCreate() {
        val question = intent.getParcelableExtra<Question>("question")

        binding.apply {
            questionAdditional.text = question?.additional
            if (question?.photoUrl != null) {
                imageWrapper.visibility = View.VISIBLE
                Picasso.get().load(question!!.photoUrl).into(questionImage)
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
                )
            }

            buttonNextQuestion.setOnClickListener {
                goToNextQuestion()
            }
        }
    }

    private fun goToNextQuestion() {
        val levelId = profile!!.checkpoint.level - 1
        val questionId = profile!!.checkpoint.question - 1

        database.getReference("quiz/levels/${levelId}")
            .child("questions/${questionId}")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("Firebase QC", snapshot.getValue().toString())
                    startActivity(
                        Intent(
                            this@QuestionCongratsActivity,
                            QuestionShowActivity::class.java
                        ).putExtra("question", snapshot.getValue<Question>())
                    )
//                    finish()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Error retrieving question", error.toException())
                }
            })
    }
}