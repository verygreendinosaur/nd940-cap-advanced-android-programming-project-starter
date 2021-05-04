package com.example.android.politicalpreparedness.election

import android.app.Application
import android.view.View
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.base.BaseViewModel
import com.example.android.politicalpreparedness.base.NavigationCommand
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.repository.CivicsRepository
import kotlinx.coroutines.launch

class ElectionDetailViewModel(val database: ElectionDao, application: Application, val election: Election): BaseViewModel(application) {

    private val electionsRepo = CivicsRepository(database)

    var voterInfo: MutableLiveData<VoterInfoResponse> = MutableLiveData()

    var isSaved: LiveData<Boolean> = Transformations.map(database.getElection(election.id)) {
        it != null
    }

    var saveButtonText: LiveData<String> = Transformations.map(isSaved) {
        if (it) "Unsave" else "Save"
    }

    fun fetchVoterInfo() {

        viewModelScope.launch {
            try {
                val result = electionsRepo.getVoterInfo(election.id)
                voterInfo.postValue(result)
            } catch (e: Exception) {
                showToastFromString.setValue(e.toString())
            }
        }

    }

    fun saveOrUnsave() {
        viewModelScope.launch {
            try {
                if (isSaved.value == true) electionsRepo.unsaveElection(election) else electionsRepo.saveElection(election)
                navigationCommand.setValue(NavigationCommand.Back())
            } catch (e: Exception) {
                showToastFromString.setValue(e.toString())

            }
        }
    }



}