package com.ivok.the_forgotten_bulgarian.views


import android.util.Log
import android.view.View
import androidx.cardview.widget.CardView
import androidx.core.view.allViews
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.ivok.the_forgotten_bulgarian.R
import com.ivok.the_forgotten_bulgarian.adapters.LettersListAdapter
import com.ivok.the_forgotten_bulgarian.databinding.ActivityQuestionShowBinding
import com.ivok.the_forgotten_bulgarian.extensions.hideLetters
import com.ivok.the_forgotten_bulgarian.extensions.randomBgLowercase
import com.ivok.the_forgotten_bulgarian.facades.AuthCompatActivity
import com.ivok.the_forgotten_bulgarian.models.Question
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.letter_card.view.*

class QuestionShowActivity : AuthCompatActivity<ActivityQuestionShowBinding>
    (R.layout.activity_question_show) {

    private var fillCounter = 1
    private val maxChars = 14
    private var answer: String? = null
    private var guessWord: StringBuilder? = null
    private var shuffled: List<Char>? = null

    override fun onCreate() {
        val question = intent.getParcelableExtra<Question>("question")

        answer = question?.answer
        guessWord = StringBuilder(answer?.hideLetters()!!)

        val randomSuffix = String.randomBgLowercase(maxChars - (answer?.length ?: 0))
        shuffled = (answer + randomSuffix).toList().shuffled()

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

            generateLetterFlexbox(recyclerLetters, shuffled!!, GuessingLetters())
            generateLetterFlexbox(guessLetters, guessWord!!.toList(), AnswerLetters())
        }
    }

    private fun generateLetterFlexbox(
        recycler: RecyclerView,
        chars: List<Char>,
        listener: LettersListAdapter.onLetterListener
    ) {
        recycler.apply {
            layoutManager = FlexboxLayoutManager(this@QuestionShowActivity).apply {
                flexDirection = FlexDirection.ROW
                justifyContent = JustifyContent.CENTER
            }
            adapter = LettersListAdapter(
                this@QuestionShowActivity,
                chars,
                listener
            )
        }
    }

    private fun initHintButton() {
        binding.buttonHint.setOnClickListener {
            binding.questionHint.visibility = View.VISIBLE
        }
    }

    inner class GuessingLetters : LettersListAdapter.onLetterListener {
        override fun onLetterClick(letterView: View?, position: Int) {
            if (fillCounter < answer!!.length - 1 &&
                letterView?.visibility == View.VISIBLE
            ) {
                letterView.visibility = View.INVISIBLE

                val letter = letterView.letter!!.text.toString()
                val index = guessWord!!.indexOfFirst { it == ' ' }

                val guessing = binding.guessLetters.getChildAt(index).letter
                guessing.text = letter
                guessWord!![index] = letter.first()

                fillCounter++
                Log.i("GuessWorld", "fillCounter: ${fillCounter}")
                Log.i("GuessWorld", "letterView: $letterView")
                Log.i("GuessWorld", "position: $position")
                Log.i("GuessWorld", guessWord.toString())
            }
        }
    }

    inner class AnswerLetters : LettersListAdapter.onLetterListener {
        override fun onLetterClick(letterView: View?, position: Int) {
            if (fillCounter >= 1 &&
                position in (1 until answer!!.length - 1) &&
                letterView?.letter?.text?.first() != ' '
            ) {
                Log.d("Guess", letterView.toString())
                Log.d("Guess", letterView?.letter.toString())
                letterView?.letter?.let { textView ->

                    binding.recyclerLetters.allViews.filter {
                        it is CardView && it.visibility == View.INVISIBLE
                    }.find {
                        it.letter.text == textView.text
                    }!!.visibility = View.VISIBLE
                    textView.text = " "
                }
                guessWord!![position] = ' '
                fillCounter--;
//                    getChildAt(
//                        shuffled!!.indexOfFirst { it == text.first() }
//                    ).visibility = View.VISIBLE
//                    text = " "
                Log.i("GuessWorld", "fillCounter: ${fillCounter}")
                Log.i("GuessWorld", "letterView: $letterView")
                Log.i("GuessWorld", "position: $position")
                Log.i("GuessWorld", guessWord.toString())
            }
        }
    }
}