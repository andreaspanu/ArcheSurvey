package it.archesurvey.app.features.survey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import it.archesurvey.app.core.common.AppResult
import it.archesurvey.app.domain.usecase.GetSurveysUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SurveyViewModel(
    private val getSurveysUseCase: GetSurveysUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(SurveyUiState())
    val uiState: StateFlow<SurveyUiState> = _uiState.asStateFlow()

    init {
        loadSurveys()
    }

    fun onEvent(event: SurveyUiEvent) {
        when (event) {
            SurveyUiEvent.Refresh -> loadSurveys()
            SurveyUiEvent.CaptureRequested -> {
                _uiState.value = _uiState.value.copy(isCaptureReady = true)
            }
        }
    }

    private fun loadSurveys() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            _uiState.value = when (val result = getSurveysUseCase(DEFAULT_PROJECT_ID)) {
                is AppResult.Success -> SurveyUiState(
                    isLoading = false,
                    surveys = result.value,
                    isCaptureReady = true
                )
                is AppResult.Error -> SurveyUiState(
                    isLoading = false,
                    errorMessage = result.reason.message
                )
            }
        }
    }

    class Factory(
        private val getSurveysUseCase: GetSurveysUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SurveyViewModel(getSurveysUseCase) as T
        }
    }

    private companion object {
        const val DEFAULT_PROJECT_ID = "local-project"
    }
}
