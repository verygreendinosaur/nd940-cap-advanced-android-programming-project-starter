package com.example.android.politicalpreparedness.election.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.R

class SavedElectionsHeaderViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    companion object {
        fun from(parent: ViewGroup): SavedElectionsHeaderViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.header_saved_elections, parent, false)
            return SavedElectionsHeaderViewHolder(view)
        }
    }

}