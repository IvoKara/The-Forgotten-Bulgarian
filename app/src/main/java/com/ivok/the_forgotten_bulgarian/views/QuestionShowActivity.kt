package com.ivok.the_forgotten_bulgarian.views

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayout
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.ivok.the_forgotten_bulgarian.R
import com.ivok.the_forgotten_bulgarian.adapters.LettersListAdapter
import com.ivok.the_forgotten_bulgarian.adapters.QuestionsListAdapter
import com.ivok.the_forgotten_bulgarian.databinding.ActivityQuestionShowBinding
import com.ivok.the_forgotten_bulgarian.extensions.appearToast
import com.ivok.the_forgotten_bulgarian.extensions.randomBgLowercase
import com.ivok.the_forgotten_bulgarian.facades.AuthCompatActivity
import com.ivok.the_forgotten_bulgarian.models.Question
import com.squareup.picasso.Picasso

class QuestionShowActivity :
    AuthCompatActivity<ActivityQuestionShowBinding>(R.layout.activity_question_show),
    LettersListAdapter.onLetterListener {

    private var fillCounter = 1

    override fun onCreate() {
        val question = intent.getParcelableExtra<Question>("question")
        val maxChars = 14

        initHintButton()

        binding.apply {
            questionText.text = question?.text
            questionHint.text = question?.hint
            if (question?.photoUrl != null) {
                imageWrapper.visibility = View.VISIBLE
                Picasso.get().load(question.photoUrl).into(questionImage)
            } else {
                imageWrapper.visibility = View.GONE
            }

            val answer = question!!.answer
            val randomSuffix = String.randomBgLowercase(maxChars - answer!!.length)
            val shuffled = (answer + randomSuffix).toList().shuffled()
            generateLetterFlexbox(recyclerLetters, shuffled)

            val guessWord = hideLetters(answer).toList()
            generateLetterFlexbox(guessLetters, guessWord)
        }
    }

    private fun generateLetterFlexbox(recycler: RecyclerView, chars: List<Char>) {
        recycler.apply {
            layoutManager = FlexboxLayoutManager(this@QuestionShowActivity).apply {
                flexDirection = FlexDirection.ROW
                justifyContent = JustifyContent.CENTER
            }
            adapter = LettersListAdapter(
                this@QuestionShowActivity,
                chars,
                this@QuestionShowActivity
            )
        }
    }

    private fun initHintButton() {
        binding.buttonHint.setOnClickListener {
            binding.questionHint.visibility = View.VISIBLE
        }
    }

    override fun onLetterClick(letterVew: View?) {
        Log.d("Letter", letterVew.toString())
        letterVew?.visibility = View.GONE
//        Log.d("Guessing", binding.guessLetters.getChildAt(fillCounter).toString())
//        val guessing = binding.guessLetters.getChildAt(fillCounter) as TextView
//        guessing.text = (letterVew as TextView).text
//        fillCounter++
    }

    private fun hideLetters(word: String): String {
        val spaces = " ".repeat(word.length - 2)
        return "${word.first()}${spaces}${word.last()}"
    }
}