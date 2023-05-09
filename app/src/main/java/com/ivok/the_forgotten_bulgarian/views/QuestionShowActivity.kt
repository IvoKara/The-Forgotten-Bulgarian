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
import com.ivok.the_forgotten_bulgarian.extensions.hideLetters
import com.ivok.the_forgotten_bulgarian.extensions.randomBgLowercase
import com.ivok.the_forgotten_bulgarian.facades.AuthCompatActivity
import com.ivok.the_forgotten_bulgarian.models.Question
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.letter_card.view.*

class QuestionShowActivity :
    AuthCompatActivity<ActivityQuestionShowBinding>(R.layout.activity_question_show),
    LettersListAdapter.onLetterListener {

    private var fillCounter = 1
    private val maxChars = 14
    private var answer: String? = null
    private var guessWord: StringBuilder? = null

    override fun onCreate() {
        val question = intent.getParcelableExtra<Question>("question")
        answer = question?.answer
        guessWord = StringBuilder(answer?.hideLetters()!!)

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

            val randomSuffix = String.randomBgLowercase(maxChars - (answer?.length ?: 0))
            val shuffled = (answer + randomSuffix).toList().shuffled()

            generateLetterFlexbox(recyclerLetters, shuffled)
            generateLetterFlexbox(guessLetters, guessWord!!.toList())
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
        if (fillCounter < answer!!.length - 1) {
            letterVew?.visibility = View.INVISIBLE
            appendNewGuessingLetter(letterVew?.letter!!.text.first())
//            val guessing = binding.guessLetters.getChildAt(fillCounter).letter
//            Log.d("Guessing", guessing.toString())
//            guessing.text = letterVew?.letter?.text
//            fillCounter++
        } else {
            return
        }
        Log.i("GuessWorld", guessWord.toString())
    }

    private fun appendNewGuessingLetter(letter: Char) {
        val guessing = binding.guessLetters.getChildAt(fillCounter).letter
        Log.d("Guessing", guessing.toString())
        guessing.text = letter.toString()
        guessWord!![fillCounter] = letter
        fillCounter++
    }

}