package com.ivok.the_forgotten_bulgarian.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ivok.the_forgotten_bulgarian.R

import com.ivok.the_forgotten_bulgarian.models.Level
import com.ivok.the_forgotten_bulgarian.models.User
import kotlinx.android.synthetic.main.level_card.view.*

class LevelsListAdapter(
    val context: Context,
    val items: List<Level>,
    val user: User,
    val listener: onLevelListener
) :
    RecyclerView.Adapter<LevelsListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.level_card,
                parent,
                false
            ), listener
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position].run {
            holder.title.text = "Ниво $number"
            holder.description.text = name
        }

        Log.d("Position", position.toString())
        holder.locked.visibility =
            if (user.checkpoint.level < (position + 1)) {
                View.VISIBLE
            } else {
                View.INVISIBLE
            }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(private val view: View, private val onLevelListener: onLevelListener) :
        RecyclerView.ViewHolder(view), View.OnClickListener {

        val title = view.level_title
        val description = view.level_description
        val locked = view.lock

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            onLevelListener.onLevelClick(items[adapterPosition], adapterPosition)
        }
    }

    interface onLevelListener {
        fun onLevelClick(level: Level, position: Int): Unit
    }
}