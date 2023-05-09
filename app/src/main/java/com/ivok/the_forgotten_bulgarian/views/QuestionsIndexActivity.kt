package com.ivok.the_forgotten_bulgarian.views

import android.content.Intent
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.ivok.the_forgotten_bulgarian.R
import com.ivok.the_forgotten_bulgarian.adapters.LevelsListAdapter
import com.ivok.the_forgotten_bulgarian.adapters.QuestionsListAdapter
import com.ivok.the_forgotten_bulgarian.databinding.ActivityQuestionsIndexBinding
import com.ivok.the_forgotten_bulgarian.extensions.appearToast
import com.ivok.the_forgotten_bulgarian.facades.AuthCompatActivity
import com.ivok.the_forgotten_bulgarian.models.Level
import com.ivok.the_forgotten_bulgarian.models.Question

class QuestionsIndexActivity :
    AuthCompatActivity<ActivityQuestionsIndexBinding>(R.layout.activity_questions_index),
    QuestionsListAdapter.onQuestionListener {

    override fun onCreate() {
        Log.d("Profile", profile.toString())

        val levelNumber = intent.getIntExtra("levelNumber", 0)
        val levelName = intent.getStringExtra("levelName")

        binding.title.text = levelName
        database.reference.child("quiz/levels/${levelNumber - 1}")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (levelName != null && snapshot.exists()) {
                        val level = snapshot.getValue<Level>()

                        Log.d("Here", profile.toString())
                        binding.recyclerQuestions.apply {
                            layoutManager = GridLayoutManager(this@QuestionsIndexActivity, 3)
                            adapter = QuestionsListAdapter(
                                this@QuestionsIndexActivity,
                                level!!.questions!!,
                                profile!!,
                                this@QuestionsIndexActivity
                            )
                        }

                        Log.d("Level", level?.questions.toString() ?: "")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("Firebase", "Error:", error.toException())
                }
            })
    }

    override fun onLevelClick(question: Question, position: Int) {
        if (profile!!.checkpoint.level >= (position + 1)) {
            val intent = Intent(this@QuestionsIndexActivity, QuestionShowActivity::class.java)
            intent.putExtra("question", question)
            startActivity(intent)
        } else {
            appearToast(this, "Все още ние си стигнал там")
        }
    }
}