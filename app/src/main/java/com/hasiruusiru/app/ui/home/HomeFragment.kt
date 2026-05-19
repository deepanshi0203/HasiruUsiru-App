package com.hasiruusiru.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.hasiruusiru.app.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            HomeViewModelFactory(requireContext())
        )[HomeViewModel::class.java]

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                binding.tvTreeCount.text = state.treeCount.toString()
                binding.tvEmptyPitCount.text = state.emptyPitCount.toString()
                binding.tvOxygenScore.text = String.format("%.1f", state.totalOxygenScore)
            }
        }

        binding.btnAddTree.setOnClickListener {
            // Navigate to Add Tree screen
            val navController = androidx.navigation.Navigation.findNavController(requireView())
            navController.navigate(com.hasiruusiru.app.R.id.action_home_to_addTree)
        }

        binding.btnViewMap.setOnClickListener {
            val navController = androidx.navigation.Navigation.findNavController(requireView())
            navController.navigate(com.hasiruusiru.app.R.id.action_home_to_map)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

