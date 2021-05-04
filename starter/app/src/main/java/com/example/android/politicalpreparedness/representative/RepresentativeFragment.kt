package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.politicalpreparedness.BuildConfig
import com.example.android.politicalpreparedness.base.BaseFragment
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListener
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import java.util.Locale

class DetailFragment : BaseFragment() {

    // region Properties

    override lateinit var viewModel: RepresentativeViewModel

    private var isAndroidQOrLater = false

    // endRegion

    // region Lifecycle / Overrides

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isAndroidQOrLater = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentRepresentativeBinding.inflate(inflater, container, false)
        val application = requireActivity().application
        val dataSource = ElectionDatabase.getInstance(application).electionDao
        val viewModelFactory = RepresentativeViewModelFactory(dataSource, application)

        viewModel = ViewModelProvider(this, viewModelFactory).get(RepresentativeViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setupFindButton(binding)
        setupLocationButton(binding)
        setupRepresentativesList(binding)

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_TURN_DEVICE_LOCATION_ON_CODE) {
            checkLocationSettings()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val isResultEmpty = grantResults.isEmpty()
        val isLocationExplicitlyDenied = grantResults[LOCATION_PERMISSION_INDEX] == PackageManager.PERMISSION_DENIED
        val isPermissionDenied = isResultEmpty || isLocationExplicitlyDenied

        if (isPermissionDenied) {
            onPermissionDenied()
        } else {
            onPermissionGranted()
        }
    }

    // endRegion

    // region Representatives List

    private fun setupRepresentativesList(binding: FragmentRepresentativeBinding) {
        val recyclerView = binding.representativesRecyclerView
        val clickListener = RepresentativeListener {
            // Do nothing at this time
        }
        val adapter = RepresentativeListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        viewModel.representatives.observe(viewLifecycleOwner, Observer {
            it.let { adapter.submitList(it) }
        })
    }

    // endRegion

    private fun onPermissionDenied() {
        val snackBar =
                Snackbar.make(requireView(), PERMISSION_DENIED_TEXT, Snackbar.LENGTH_INDEFINITE)
                        .setAction(SETTINGS_TEXT) {
                            startActivity(Intent().apply {
                                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            })
                        }

        viewModel.showSnackBar.value = snackBar
    }

    private fun onPermissionGranted() {
        checkLocationSettings()
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private fun setupFindButton(binding: FragmentRepresentativeBinding) {
        binding.buttonSearch.setOnClickListener {
            viewModel.findReps()
            hideKeyboard()
        }
    }

    private fun setupLocationButton(binding: FragmentRepresentativeBinding) {
        binding.buttonLocation.setOnClickListener {
            checkLocationPermissions()
            hideKeyboard()
        }
    }

    private fun checkLocationPermissions() {
        if (isPermissionGranted()) {
            checkLocationSettings()
        } else {
            requestLocationPermissions()
        }
    }

    private fun checkLocationSettings() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        val task = settingsClient.checkLocationSettings(builder.build())

        listenForLocationSettingsFailure(task)
        listenForLocationSettingsSuccess(task)
    }

    private fun listenForLocationSettingsFailure(task: Task<LocationSettingsResponse>, resolve: Boolean = true) {
        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve) {
                onResolvableException(exception, resolve)
            } else {
                onNonResolvableException()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun listenForLocationSettingsSuccess(task: Task<LocationSettingsResponse>) {
        task.addOnCompleteListener {
            if (it.isSuccessful) {
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
                fusedLocationClient.lastLocation
                        .addOnSuccessListener {
                            val geoCoder = Geocoder(context, Locale.getDefault())
                            if (it != null) {
                                val geoResults = geoCoder.getFromLocation(it.latitude, it.longitude, 1).firstOrNull()
                                if (geoResults != null) {
                                    viewModel.findRepsFromLocation(geoResults)
                                }
                            }
                        }
            } else {
                // Do nothing at this time.
            }
        }
    }

    private fun onResolvableException(exception: ResolvableApiException, resolve: Boolean = true) {
        try {
            exception.startResolutionForResult(activity, REQUEST_TURN_DEVICE_LOCATION_ON_CODE)
        } catch (sendEx: IntentSender.SendIntentException) {
            // Do nothing for now.
        }
    }

    private fun onNonResolvableException() {
        Snackbar.make(requireView(), LOCATION_SETTINGS_TEXT, Snackbar.LENGTH_INDEFINITE)
                .setAction(android.R.string.ok) {
                    checkLocationSettings()
                }
                .show()
    }

    private fun requestLocationPermissions() {
        if (isForegroundLocationPermissionGranted()) {
            return
        }

        var permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        var requestCode = REQUEST_FOREGROUND_ONLY_PERMISSIONS_CODE
        requestPermissions(permissions, 100)
    }

    private fun isForegroundLocationPermissionGranted(): Boolean {
        val selfPermission = ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
        return PackageManager.PERMISSION_GRANTED == selfPermission
    }

    private fun isPermissionGranted(): Boolean {
        return isForegroundLocationPermissionGranted()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

    // region Constants

    companion object {
        private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_CODE = 34
        private const val REQUEST_TURN_DEVICE_LOCATION_ON_CODE = 29
        private const val LOCATION_PERMISSION_INDEX = 0
        private const val PERMISSION_DENIED_TEXT = "Location permissions denied. Please enable for best experience."
        private const val LOCATION_SETTINGS_TEXT = "Something went wrong enabling location services. Please try again."
        private const val SETTINGS_TEXT = "Settings"
    }

    // endRegion

}