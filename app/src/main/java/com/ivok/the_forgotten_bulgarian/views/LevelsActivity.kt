package com.ivok.the_forgotten_bulgarian.views

import android.content.Intent
import android.os.Parcelable
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.ivok.the_forgotten_bulgarian.R
import com.ivok.the_forgotten_bulgarian.adapters.LevelsListAdapter
import com.ivok.the_forgotten_bulgarian.databinding.ActivityLevelsBinding
import com.ivok.the_forgotten_bulgarian.facades.AuthCompatActivity
import com.ivok.the_forgotten_bulgarian.models.Level
import com.ivok.the_forgotten_bulgarian.models.Question
import java.util.ArrayList

class LevelsActivity :
    AuthCompatActivity<ActivityLevelsBinding>(R.layout.activity_levels),
    LevelsListAdapter.onLevelListener {

    override fun onCreate() {
        database.reference.child("quiz/levels")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val levels = dataSnapshot.getValue<List<Level>>()

                    binding.recyclerLevels.apply {
                        layoutManager = LinearLayoutManager(this@LevelsActivity)
                        adapter = LevelsListAdapter(
                            this@LevelsActivity, levels!!, this@LevelsActivity
                        )
                    }
                    Log.d("Firebase", levels.toString())
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w("Firebase", "Error:", databaseError.toException())
                }
            })
    }

    override fun onLevelClick(level: Level) {
        Log.d("LevelsAdapter", level.toString())
        val intent = Intent(this, QuestionsIndexActivity::class.java)
        intent.putExtra("levelNumber", level.number)
        intent.putExtra("levelName", level.name)
        startActivity(intent)
    }
}