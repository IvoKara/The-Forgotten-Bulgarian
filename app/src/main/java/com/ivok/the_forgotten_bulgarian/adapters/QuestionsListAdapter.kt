package com.ivok.the_forgotten_bulgarian.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ivok.the_forgotten_bulgarian.R

import com.ivok.the_forgotten_bulgarian.models.Level
import com.ivok.the_forgotten_bulgarian.models.Question
import kotlinx.android.synthetic.main.question_card.view.*

class QuestionsListAdapter(
    val context: Context,
    val items: List<Question>,
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
            onQuestionListener.onLevelClick(items[adapterPosition])
        }
    }

    interface onQuestionListener {
        fun onLevelClick(question: Question): Unit
    }
}