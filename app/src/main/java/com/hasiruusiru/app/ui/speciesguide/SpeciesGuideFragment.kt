package com.hasiruusiru.app.ui.speciesguide

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hasiruusiru.app.data.repository.TreeRepository
import com.hasiruusiru.app.databinding.FragmentSpeciesGuideBinding

class SpeciesGuideFragment : Fragment() {

    private var _binding: FragmentSpeciesGuideBinding? = null
    private val binding get() = _binding!!

    private var isKannada = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSpeciesGuideBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = TreeRepository(requireContext())
        val adapter = SpeciesAdapter(repository.speciesList, isKannada)

        binding.rvSpecies.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSpecies.adapter = adapter

        // Language toggle button
        binding.btnToggleLanguage.setOnClickListener {
            isKannada = !isKannada
            binding.btnToggleLanguage.text = if (isKannada) "English" else "ಕನ್ನಡ"
            adapter.toggleLanguage(isKannada)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
