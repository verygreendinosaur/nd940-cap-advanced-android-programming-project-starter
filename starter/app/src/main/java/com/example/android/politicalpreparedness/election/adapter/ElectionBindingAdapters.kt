package com.example.android.politicalpreparedness.election.adapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("electionDay")
fun bindElectionDay(
        view: TextView,
        electionDay: Date
) {
    val format = SimpleDateFormat(ElectionViewHolder.DATE_FORMAT)
    view.text = format.format(electionDay)
}
