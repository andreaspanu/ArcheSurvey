package it.archesurvey.app.features.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun onEvent(event: HomeUiEvent) {
        _uiState.value = when (event) {
            HomeUiEvent.ProjectsSelected,
            HomeUiEvent.SettingsSelected,
            HomeUiEvent.AboutSelected -> _uiState.value.copy(isReady = true)
        }
    }
}
