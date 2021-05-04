package com.example.android.politicalpreparedness.election.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.R
import kotlinx.android.synthetic.main.header_upcoming_elections.view.*

class ElectionHeaderViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    companion object {
        fun from(parent: ViewGroup, headerText: String): ElectionHeaderViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.header_upcoming_elections, parent, false)
            view.header_text.text = headerText
            return ElectionHeaderViewHolder(view)
        }
    }

}