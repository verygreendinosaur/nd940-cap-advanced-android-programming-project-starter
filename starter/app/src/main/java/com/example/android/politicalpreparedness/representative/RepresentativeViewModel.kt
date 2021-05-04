package com.example.android.politicalpreparedness.representative

import android.app.Application
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.base.BaseViewModel
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.repository.CivicsRepository
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch
import java.lang.Exception

class RepresentativeViewModel(val database: ElectionDao, application: Application) : BaseViewModel(application) {

    // region Properties

    private val civicsRepo = CivicsRepository(database)

    val representatives: MutableLiveData<List<Representative>> = MutableLiveData()
    val addressLineOne: MutableLiveData<String> = MutableLiveData<String>("")
    val addressLineTwo: MutableLiveData<String> = MutableLiveData<String>("")
    val addressCity: MutableLiveData<String> = MutableLiveData<String>("")
    val addressState: MutableLiveData<String> = MutableLiveData<String>("")
    val addressZip: MutableLiveData<String> = MutableLiveData<String>("")

    // endRegion

    // region Representatives

    fun findRepsFromLocation(location: android.location.Address) {
        addressLineOne.value = location.subThoroughfare + " " + location.thoroughfare
        addressLineTwo.value = location.getAddressLine(1)
        addressCity.value = location.locality
        addressState.value = location.adminArea
        addressZip.value = location.postalCode

        findReps()
    }

    fun findReps() {
        val address = validateAndBuildAddress()
        if (address == null) {
            showToastFromString.setValue("Oops -- something went wrong retrieving your location. Please try again later.")
        } else {
            fetchRep(address)
        }
    }

    private fun fetchRep(address: Address) {
        viewModelScope.launch {
            try {
                val result = civicsRepo.getReps(address)
                representatives.postValue(result)
            } catch (e: Exception) {
            }
        }
    }

    // endRegion

    // region Address

    fun validateAndBuildAddress(): Address? {
        val addressOne = addressLineOne.value
        val addressCity = addressCity.value
        val addressState = addressState.value
        val addressZip = addressZip.value

        if (addressOne != null && addressCity != null && addressState != null && addressZip != null) {
            return Address(addressOne, addressLineTwo.value, addressCity, addressState, addressZip)
        } else {
            return null
        }
    }

    // endRegion

}
