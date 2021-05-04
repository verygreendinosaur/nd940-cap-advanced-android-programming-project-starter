package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.politicalpreparedness.base.BaseFragment
import com.example.android.politicalpreparedness.base.NavigationCommand
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener

class ElectionsFragment: BaseFragment() {

    override lateinit var viewModel: ElectionsViewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate view and obtain an instance of the binding class.
        val binding = FragmentElectionBinding.inflate(inflater, container, false)

        // Specify the current activity as the lifecycle owner.
        binding.lifecycleOwner = this

        val application = requireActivity().application
        val dataSource = ElectionDatabase.getInstance(application).electionDao
        val viewModelFactory = ElectionsViewModelFactory(dataSource, application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ElectionsViewModel::class.java)

        binding.viewModel = viewModel

        setupUpcomingElectionsList(binding)
        setupSavedElectionsList(binding)

        viewModel.fetchElections()
        return binding.root
    }

    private fun setupUpcomingElectionsList(binding: FragmentElectionBinding) {
        val recyclerView = binding.upcomingElectionsRecyclerView
        val clickListener = ElectionListener {
            val action = ElectionsFragmentDirections.actionElectionsFragmentToElectionDetailFragment(it)
            viewModel.navigationCommand.setValue(NavigationCommand.To(action))
        }
        val adapter = ElectionListAdapter(clickListener, UPCOMING_ELECTIONS_HEADER_TEXT)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        viewModel.elections.observe(viewLifecycleOwner, Observer {
            it.let { adapter.submitListWithHeader(it) }
        })
    }

    private fun setupSavedElectionsList(binding: FragmentElectionBinding) {
        val recyclerView = binding.savedElectionsRecyclerView
        val clickListener = ElectionListener {
            val action = ElectionsFragmentDirections.actionElectionsFragmentToElectionDetailFragment(it)
            viewModel.navigationCommand.setValue(NavigationCommand.To(action))
        }
        val adapter = ElectionListAdapter(clickListener, SAVED_ELECTIONS_HEADER_TEXT)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        viewModel.savedElections.observe(viewLifecycleOwner, Observer {
            it.let { adapter.submitListWithHeader(it) }
        })
    }

    companion object {
        const val UPCOMING_ELECTIONS_HEADER_TEXT = "Upcoming Elections"
        const val SAVED_ELECTIONS_HEADER_TEXT = "Saved"
    }

}