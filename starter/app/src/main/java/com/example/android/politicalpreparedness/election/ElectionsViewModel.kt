package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.base.BaseViewModel
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.repository.CivicsRepository
import kotlinx.coroutines.launch


class ElectionsViewModel(val database: ElectionDao, application: Application): BaseViewModel(application) {

    private val civicsRepo = CivicsRepository(database)

    val elections: MutableLiveData<List<Election>> = MutableLiveData()
    val savedElections: LiveData<List<Election>> = civicsRepo.savedElections

    init {
        fetchElections()
    }

    fun fetchElections() {
        viewModelScope.launch {
            try {
                var result = civicsRepo.getElections()
                elections.postValue(result)
            } catch (e: Exception) {
                showToastFromString.value = "Something went wrong while fetching elections. Please try again later."
            }
        }
    }

}
