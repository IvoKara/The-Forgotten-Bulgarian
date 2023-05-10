package com.ivok.the_forgotten_bulgarian.views


import android.content.Intent
import android.util.Log
import android.view.View
import androidx.cardview.widget.CardView
import androidx.core.os.HandlerCompat
import androidx.core.view.allViews
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.ivok.the_forgotten_bulgarian.R
import com.ivok.the_forgotten_bulgarian.adapters.LettersListAdapter
import com.ivok.the_forgotten_bulgarian.databinding.ActivityQuestionShowBinding
import com.ivok.the_forgotten_bulgarian.extensions.appearToast
import com.ivok.the_forgotten_bulgarian.extensions.hideLetters
import com.ivok.the_forgotten_bulgarian.extensions.randomBgLowercase
import com.ivok.the_forgotten_bulgarian.facades.AuthCompatActivity
import com.ivok.the_forgotten_bulgarian.models.Checkpoint
import com.ivok.the_forgotten_bulgarian.models.Question
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.letter_card.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.logging.Handler

class QuestionShowActivity : AuthCompatActivity<ActivityQuestionShowBinding>
    (R.layout.activity_question_show) {

    private var fillCounter = 0
    private val maxChars = 14
    private var question: Question? = null
    private var guessWord: StringBuilder? = null
    private var shuffled: List<Char>? = null

    override fun onCreate() {
        question = intent.getParcelableExtra<Question>("question")

        guessWord = StringBuilder(question?.answer?.hideLetters()!!)

        val randomSuffix = String.randomBgLowercase(maxChars - (question?.answer?.length ?: 0))
        shuffled = (question?.answer + randomSuffix).toList().shuffled()

        initHintButton()

        binding.apply {
            questionText.text = question?.text
            questionHint.text = question?.hint
            if (question?.photoUrl != null) {
                imageWrapper.visibility = View.VISIBLE
                Picasso.get().load(question!!.photoUrl).into(questionImage)
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

    private fun checkIsGuessValid() {
        if (question?.answer == guessWord.toString()) {

            Log.w("Firebase Profile", profile.toString())
            Log.w("Firebase Question Level", question?.level.toString())
            Log.w("Firebase Question Nbr", question?.number.toString())

            question!!.run {
                val currentCheckpoint = Checkpoint(level!!, number!!)
                moveUserToNextQuestion(currentCheckpoint) {
                    showCongratsActivity()
                }
            }
        } else {
            binding.validation.visibility = View.VISIBLE
        }
    }

    private fun showCongratsActivity() {
        val intent = Intent(this@QuestionShowActivity, QuestionCongratsActivity::class.java)
        intent.putExtra("question", question)
        startActivity(intent)
        finish()
    }

    inner class GuessingLetters : LettersListAdapter.onLetterListener {
        override fun onLetterClick(letterView: View?, position: Int) {

            if (fillCounter < question?.answer!!.length - 2 &&
                letterView?.visibility == View.VISIBLE
            ) {
                binding.validation.visibility = View.INVISIBLE
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

            if (fillCounter == question?.answer!!.length - 2) {
                checkIsGuessValid()
            }
        }
    }

    inner class AnswerLetters : LettersListAdapter.onLetterListener {
        override fun onLetterClick(letterView: View?, position: Int) {

            if (fillCounter > 0 &&
                position in (1 until question?.answer!!.length - 1) &&
                letterView?.letter?.text?.first() != ' '
            ) {
                binding.validation.visibility = View.INVISIBLE

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

                Log.i("GuessWorld", "fillCounter: ${fillCounter}")
                Log.i("GuessWorld", "letterView: $letterView")
                Log.i("GuessWorld", "position: $position")
                Log.i("GuessWorld", guessWord.toString())
            }
        }
    }
}