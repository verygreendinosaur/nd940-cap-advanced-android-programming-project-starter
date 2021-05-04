package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.models.Election
import java.lang.IllegalArgumentException

class ElectionsViewModelFactory(
        private val dataSource: ElectionDao,
        private val application: Application
): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ElectionsViewModel::class.java)) {
            return ElectionsViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}

class ElectionDetailViewModelFactory(
        private val dataSource: ElectionDao,
        private val application: Application,
        private val election: Election
): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ElectionDetailViewModel::class.java)) {
            return ElectionDetailViewModel(dataSource, application, election) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}