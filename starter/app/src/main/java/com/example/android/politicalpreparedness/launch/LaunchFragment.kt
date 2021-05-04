package com.example.android.politicalpreparedness.launch

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.databinding.FragmentLaunchBinding

class LaunchFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentLaunchBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this
        setupElectionsButton(binding)
        setupFindRepsButton(binding)

        return binding.root
    }

    private fun setupElectionsButton(binding: FragmentLaunchBinding) {
        binding.upcomingElectionsButton.setOnClickListener {
            navToElections()
        }
    }

    private fun setupFindRepsButton(binding: FragmentLaunchBinding) {
        binding.findRepsButton.setOnClickListener {
            navToRepresentatives()
        }
    }

    private fun navToElections() {
        findNavController().navigate(LaunchFragmentDirections.actionLaunchFragmentToElectionsFragment())
    }

    private fun navToRepresentatives() {
        this.findNavController().navigate(LaunchFragmentDirections.actionLaunchFragmentToRepresentativeFragment())
    }

}
