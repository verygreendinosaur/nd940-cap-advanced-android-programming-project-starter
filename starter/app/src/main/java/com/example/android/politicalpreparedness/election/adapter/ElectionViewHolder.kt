package com.example.android.politicalpreparedness.election.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.databinding.ListItemElectionBinding
import java.text.SimpleDateFormat
import java.util.*

class ElectionViewHolder(val binding: ListItemElectionBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(clickListener: ElectionListener, item: ElectionDataItem.ElectionItem) {
        binding.election = item.election
        binding.electionClickListener = clickListener
        binding.executePendingBindings()
    }

    companion object {

        const val DATE_FORMAT = "E, MMM dd, yyyy"

        fun from(parent: ViewGroup): ElectionViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ListItemElectionBinding.inflate(layoutInflater, parent, false)
            return ElectionViewHolder(binding)
        }

    }

}