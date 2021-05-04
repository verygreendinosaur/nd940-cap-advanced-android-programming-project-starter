package com.example.android.politicalpreparedness.election

import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.Observer
import com.example.android.politicalpreparedness.base.BaseFragment
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionDetailBinding

class ElectionDetailFragment : BaseFragment() {

    override lateinit var viewModel: ElectionDetailViewModel
    var builder = CustomTabsIntent.Builder()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentElectionDetailBinding.inflate(inflater, container, false)

        // Specify bundle args
        val bundle = arguments
        if (bundle != null) {
            val application = requireActivity().application
            val dataSource = ElectionDatabase.getInstance(application).electionDao
            val args = ElectionDetailFragmentArgs.fromBundle(bundle)
            val election = args.election

            val viewModelFactory = ElectionDetailViewModelFactory(dataSource, application, election)
            viewModel = ViewModelProvider(this, viewModelFactory).get(ElectionDetailViewModel::class.java)

            binding.viewModel = viewModel
        }

        binding.lifecycleOwner = this

        setupGeneralInformationLink(binding)
        setupVotingLocationsLink(binding)
        setupBallotInformationLink(binding)
        setupSaveButton(binding)

        viewModel.fetchVoterInfo()

        return binding.root
    }

    private fun setupGeneralInformationLink(binding: FragmentElectionDetailBinding) {
        binding.generalInfoTextView.visibility = View.GONE

        viewModel.voterInfo.observe(viewLifecycleOwner, Observer { voterInfo ->
            val uri = voterInfo.state?.first()?.electionAdministrationBody?.electionInfoUrl
            val visibility = if (uri == null) View.GONE else View.VISIBLE
            binding.generalInfoTextView.visibility = visibility

            binding.generalInfoTextView.setOnClickListener {
                launchCustomTabs(uri)
            }
        })
    }

    private fun setupVotingLocationsLink(binding: FragmentElectionDetailBinding) {
        binding.votingLocationsTextView.visibility = View.GONE

        viewModel.voterInfo.observe(viewLifecycleOwner, Observer { voterInfo ->
            val uri = voterInfo.state?.first()?.electionAdministrationBody?.votingLocationFinderUrl
            val visibility = if (uri == null) View.GONE else View.VISIBLE
            binding.votingLocationsTextView.visibility = visibility

            binding.votingLocationsTextView.setOnClickListener {
                launchCustomTabs(uri)
            }
        })
    }

    private fun setupBallotInformationLink(binding: FragmentElectionDetailBinding) {
        binding.ballotInfoTextView.visibility = View.GONE

        viewModel.voterInfo.observe(viewLifecycleOwner, Observer { voterInfo ->
            val uri = voterInfo.state?.first()?.electionAdministrationBody?.ballotInfoUrl
            val visibility = if (uri == null) View.GONE else View.VISIBLE
            binding.ballotInfoTextView.visibility = visibility

            binding.ballotInfoTextView.setOnClickListener {
                launchCustomTabs(uri)
            }
        })
    }

    fun safeParse(uriString: String?): Uri? {
        try {
            val uri = Uri.parse(uriString)
            return uri
        } catch (e: Exception) {
            return null
        }

    }

    private fun launchCustomTabs(urlString: String?) {
        val parsedUri = safeParse(urlString)

        if (parsedUri == null) {
            viewModel.showToastFromString.value = "We couldn't find a valid web address for this platform."
        } else {
            var customTabsIntent = builder.build()
            customTabsIntent.launchUrl(requireContext(), parsedUri)
        }
    }

    private fun setupSaveButton(binding: FragmentElectionDetailBinding) {
        binding.saveElectionButton.setOnClickListener {
            viewModel.saveOrUnsave()
        }
    }

}