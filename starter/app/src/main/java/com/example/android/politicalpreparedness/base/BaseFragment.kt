package com.example.android.politicalpreparedness.base

import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController

/**
 * Base Fragment. Observes common LiveData objects from BaseViewModel.
 */
abstract class BaseFragment : Fragment() {

    abstract val viewModel: BaseViewModel

    // region Lifecycle / Overrides

    override fun onStart() {
        super.onStart()
        observeNavigationCommand()
        observeShowSnackBar()
        observeShowToast()
        observeShowToastFromString()
    }

    // endregion

    // region Observers

    private fun observeNavigationCommand() {
        viewModel.navigationCommand.observe(this, Observer { command ->
            when (command) {
                is NavigationCommand.To -> findNavController().navigate(command.directions)
                is NavigationCommand.Back -> findNavController().popBackStack()
            }
        })
    }

    private fun observeShowSnackBar() {
        viewModel.showSnackBar.observe(this, Observer {
            it.show()
        })
    }

    private fun observeShowToast() {
        viewModel.showToast.observe(this, Observer {
            it.show()
        })
    }

    private fun observeShowToastFromString() {
        viewModel.showToastFromString.observe(this, Observer {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        })
    }

    // endregion

}
