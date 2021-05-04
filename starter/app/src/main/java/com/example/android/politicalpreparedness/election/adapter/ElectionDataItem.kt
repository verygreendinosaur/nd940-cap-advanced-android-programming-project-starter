package com.example.android.politicalpreparedness.election.adapter

import com.example.android.politicalpreparedness.network.models.Election

/*
 * Based on the approach recommended by Google at
 * https://developer.android.com/codelabs/kotlin-android-training-headers
 */
sealed class ElectionDataItem {

    abstract val id: Int

    data class ElectionItem(val election: Election) : ElectionDataItem() {
        override val id = election.id
    }

    object Header : ElectionDataItem() {
        override val id = Int.MIN_VALUE
    }

}