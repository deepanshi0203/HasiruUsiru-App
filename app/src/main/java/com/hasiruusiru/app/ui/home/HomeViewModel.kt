package com.hasiruusiru.app.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hasiruusiru.app.data.repository.TreeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val treeCount: Int = 0,
    val emptyPitCount: Int = 0,
    val totalOxygenScore: Double = 0.0
)

class HomeViewModel(private val repository: TreeRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        viewModelScope.launch {
            repository.trees.collect { trees ->
                _uiState.value = HomeUiState(
                    treeCount = trees.size,
                    emptyPitCount = trees.count { it.isEmptyPit },
                    totalOxygenScore = trees.sumOf { it.oxygenScore }
                )
            }
        }
    }
}

class HomeViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return HomeViewModel(TreeRepository(context)) as T
    }
}

