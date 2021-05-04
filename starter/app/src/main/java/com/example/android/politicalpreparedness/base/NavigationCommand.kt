package com.example.android.politicalpreparedness.base

import androidx.navigation.NavDirections

sealed class NavigationCommand {

    data class To(val directions: NavDirections) : NavigationCommand()
    data class Back(val directions: NavDirections? = null) : NavigationCommand()

}

