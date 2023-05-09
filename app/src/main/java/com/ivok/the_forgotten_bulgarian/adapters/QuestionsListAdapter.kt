package com.ivok.the_forgotten_bulgarian.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ivok.the_forgotten_bulgarian.R

import com.ivok.the_forgotten_bulgarian.models.Level
import com.ivok.the_forgotten_bulgarian.models.Question
import com.ivok.the_forgotten_bulgarian.models.User
import kotlinx.android.synthetic.main.question_card.view.*

class QuestionsListAdapter(
    val context: Context,
    val items: List<Question>,
    val user: User,
    val listener: onQuestionListener
) :
    RecyclerView.Adapter<QuestionsListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.question_card,
                parent,
                false
            ), listener
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.questionNumber.text = "#${position + 1}"


        if (user.checkpoint.level >= (position + 1)) {
            holder.itemView.backgroundTintList =
                AppCompatResources.getColorStateList(context, R.color.red_brown_300)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(
        private val view: View,
        private val onQuestionListener: onQuestionListener
    ) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val questionNumber = view.question_text

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            onQuestionListener.onLevelClick(items[adapterPosition], adapterPosition)
        }
    }

    interface onQuestionListener {
        fun onLevelClick(question: Question, position: Int): Unit
    }
}