package com.ivok.the_forgotten_bulgarian.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ivok.the_forgotten_bulgarian.R

import com.ivok.the_forgotten_bulgarian.models.Level
import kotlinx.android.synthetic.main.letter_card.view.*

class LettersListAdapter(
    val context: Context,
    val items: List<Char>,
    val listener: onLetterListener
) :
    RecyclerView.Adapter<LettersListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.letter_card,
                parent,
                false
            ), listener
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position].run {
            holder.letter.text = this.toString()
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(private val view: View, private val onLetterListener: onLetterListener) :
        RecyclerView.ViewHolder(view), View.OnClickListener {

        val letter = view.letter

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            onLetterListener.onLetterClick(view, adapterPosition)
        }
    }

    interface onLetterListener {
        fun onLetterClick(letterView: View?, position: Int): Unit
    }
}