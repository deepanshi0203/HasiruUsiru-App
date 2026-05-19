package com.hasiruusiru.app.ui.addtree

import com.hasiruusiru.app.R
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.hasiruusiru.app.data.model.Tree
import com.hasiruusiru.app.data.repository.TreeRepository
import com.hasiruusiru.app.databinding.FragmentAddTreeBinding
import kotlinx.coroutines.launch

class AddTreeFragment : Fragment() {

    private var _binding: FragmentAddTreeBinding? = null
    private val binding get() = _binding!!

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLat = 0.0
    private var currentLng = 0.0
    private lateinit var repository: TreeRepository

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            fetchLocation()
        } else {
            Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTreeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repository = TreeRepository(requireContext())
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Populate species spinner
        val speciesNames = repository.speciesList.map { it.nameEnglish }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, speciesNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSpecies.adapter = adapter

        // Fetch GPS location
        binding.btnGetLocation.setOnClickListener {
            requestLocationPermission()
        }

        // Toggle Empty Pit checkbox - hide girth input if empty pit
        binding.checkboxEmptyPit.setOnCheckedChangeListener { _, isChecked ->
            binding.tilGirth.visibility = if (isChecked) View.GONE else View.VISIBLE
        }

        // Save tree
        binding.btnSaveTree.setOnClickListener {
            saveTree()
        }
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fetchLocation()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun fetchLocation() {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    currentLat = location.latitude
                    currentLng = location.longitude
                    binding.tvLocationStatus.text =
                        "📍 ${String.format("%.5f", currentLat)}, ${String.format("%.5f", currentLng)}"
                    binding.tvLocationStatus.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.green_primary)
                    )
                } else {
                    // Request fresh location if lastLocation is null
                    val locationRequest = com.google.android.gms.location.LocationRequest.create().apply {
                        priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
                        numUpdates = 1
                        interval = 1000
                    }
                    fusedLocationClient.requestLocationUpdates(
                        locationRequest,
                        object : com.google.android.gms.location.LocationCallback() {
                            override fun onLocationResult(result: com.google.android.gms.location.LocationResult) {
                                val loc = result.lastLocation
                                if (loc != null) {
                                    currentLat = loc.latitude
                                    currentLng = loc.longitude
                                    binding.tvLocationStatus.text =
                                        "📍 ${String.format("%.5f", currentLat)}, ${String.format("%.5f", currentLng)}"
                                    binding.tvLocationStatus.setTextColor(
                                        ContextCompat.getColor(requireContext(), R.color.green_primary)
                                    )
                                    fusedLocationClient.removeLocationUpdates(this)
                                }
                            }
                        },
                        android.os.Looper.getMainLooper()
                    )
                }
            }
        } catch (e: SecurityException) {
            Toast.makeText(requireContext(), "Location permission needed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveTree() {
        val speciesIndex = binding.spinnerSpecies.selectedItemPosition
        val species = repository.speciesList[speciesIndex]
        val isEmptyPit = binding.checkboxEmptyPit.isChecked
        val girthText = binding.etGirth.text.toString()

        if (currentLat == 0.0 && currentLng == 0.0) {
            Toast.makeText(requireContext(), "Please get your GPS location first!", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isEmptyPit && girthText.isBlank()) {
            Toast.makeText(requireContext(), "Please enter the tree girth", Toast.LENGTH_SHORT).show()
            return
        }

        val girth = if (isEmptyPit) 0.0 else girthText.toDoubleOrNull() ?: 0.0

        val tree = Tree(
            latitude = currentLat,
            longitude = currentLng,
            speciesName = species.nameEnglish,
            speciesNameKannada = species.nameKannada,
            girth = girth,
            health = when (binding.rgHealth.checkedRadioButtonId) {
                com.hasiruusiru.app.R.id.rb_health_good -> "Good"
                com.hasiruusiru.app.R.id.rb_health_fair -> "Fair"
                else -> "Poor"
            },
            isEmptyPit = isEmptyPit,
            address = binding.etAddress.text.toString()
        )

        repository.addTree(tree)
        Toast.makeText(requireContext(), "🌳 Tree tagged successfully!", Toast.LENGTH_SHORT).show()
        requireActivity().onBackPressed()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

