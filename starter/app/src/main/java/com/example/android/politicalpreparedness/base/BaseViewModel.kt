package com.example.android.politicalpreparedness.base

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.google.android.material.snackbar.Snackbar

abstract class BaseViewModel(app: Application) : AndroidViewModel(app) {

    val navigationCommand: SingleLiveEvent<NavigationCommand> = SingleLiveEvent()
    val showSnackBar: SingleLiveEvent<Snackbar> = SingleLiveEvent()
    val showToast: SingleLiveEvent<Toast> = SingleLiveEvent()
    val showToastFromString: SingleLiveEvent<String> = SingleLiveEvent()

}