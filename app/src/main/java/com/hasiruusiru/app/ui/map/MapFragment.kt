package com.hasiruusiru.app.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.hasiruusiru.app.R
import com.hasiruusiru.app.data.repository.TreeRepository
import com.hasiruusiru.app.databinding.FragmentMapBinding
import kotlinx.coroutines.launch

class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var googleMap: GoogleMap
    private lateinit var repository: TreeRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repository = TreeRepository(requireContext())

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Default camera position: Bengaluru
        val bengaluru = LatLng(12.9716, 77.5946)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bengaluru, 12f))
        googleMap.uiSettings.isZoomControlsEnabled = true

        // Load all trees and place markers
        viewLifecycleOwner.lifecycleScope.launch {
            repository.trees.collect { trees ->
                googleMap.clear()
                trees.forEach { tree ->
                    val position = LatLng(tree.latitude, tree.longitude)
                    val markerColor = if (tree.isEmptyPit)
                        BitmapDescriptorFactory.HUE_RED
                    else
                        BitmapDescriptorFactory.HUE_GREEN

                    googleMap.addMarker(
                        MarkerOptions()
                            .position(position)
                            .title(if (tree.isEmptyPit) "Empty Pit" else tree.speciesName)
                            .snippet(if (tree.isEmptyPit) "Needs a tree!" else "O₂ Score: ${tree.oxygenScore}")
                            .icon(BitmapDescriptorFactory.defaultMarker(markerColor))
                    )
                }

                // Update stats in the overlay
                binding.tvMapTreeCount.text = "🌳 ${trees.size} Trees"
                binding.tvMapEmptyPits.text = "🔴 ${trees.count { it.isEmptyPit }} Empty Pits"
                binding.tvMapOxygen.text = "O₂ Score: ${String.format("%.1f", trees.sumOf { it.oxygenScore })}"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
