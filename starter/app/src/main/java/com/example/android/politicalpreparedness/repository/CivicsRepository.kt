package com.example.android.politicalpreparedness.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.*
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CivicsRepository(private val database: ElectionDao) : ViewModel() {

    // region Properties

    val savedElections: LiveData<List<Election>> = Transformations
            .map(database.getElections()) { it }

    // endregion

    // region Elections

    suspend fun getElections(): List<Election> {
        return CivicsApi.retrofitService.getElections().elections
    }

    suspend fun saveElection(election: Election) {
        withContext(Dispatchers.IO) {
            database.insert(election)
        }
    }

    suspend fun unsaveElection(election: Election) {
        withContext(Dispatchers.IO) {
            database.delete(election.id)
        }
    }

    // endregion

    // region Voter Info

    suspend fun getVoterInfo(electionId: Int): VoterInfoResponse {
        return CivicsApi.retrofitService.getVoterInfo(electionId, "1600 Amphitheatre Parkway Mountain View, CA 94043")
    }

    // endregion

    // region Representatives

    suspend fun getReps(address: Address): List<Representative> {
        val result = CivicsApi.retrofitService.getRepInfo(address.toFormattedString())
        return result.offices
                .flatMap {
                    it.getRepresentatives(result.officials)
                }
    }

    // endregion

}