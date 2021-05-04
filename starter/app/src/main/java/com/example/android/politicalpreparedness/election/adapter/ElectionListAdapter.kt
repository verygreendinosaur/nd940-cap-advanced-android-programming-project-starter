package com.example.android.politicalpreparedness.election.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.network.models.Election

class ElectionListAdapter(private val clickListener: ElectionListener, private val headerText: String) : ListAdapter<ElectionDataItem, RecyclerView.ViewHolder>(ElectionDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ELECTION = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> ElectionHeaderViewHolder.from(parent, headerText)
            else -> ElectionViewHolder.from(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ElectionViewHolder -> {
                val electionItem = getItem(position) as ElectionDataItem.ElectionItem
                holder.bind(clickListener, electionItem)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ElectionDataItem.Header -> VIEW_TYPE_HEADER
            else -> VIEW_TYPE_ELECTION
        }
    }

    fun submitListWithHeader(list: List<Election>?) {
        val items = when (list) {
            null -> listOf(ElectionDataItem.Header)
            else -> listOf(ElectionDataItem.Header) + list.map { ElectionDataItem.ElectionItem(it) }
        }

        submitList(items)
    }

}

class ElectionListener(val clickListener: (election: Election) -> Unit) {

    fun onClick(election: Election) = clickListener(election)

}


class ElectionDiffCallback : DiffUtil.ItemCallback<ElectionDataItem>() {

    override fun areItemsTheSame(oldItem: ElectionDataItem, newItem: ElectionDataItem): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ElectionDataItem, newItem: ElectionDataItem): Boolean {
        return oldItem == newItem
    }

}
