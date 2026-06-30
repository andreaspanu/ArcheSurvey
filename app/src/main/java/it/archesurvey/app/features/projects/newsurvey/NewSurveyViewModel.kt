package it.archesurvey.app.features.projects.newsurvey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import it.archesurvey.app.core.common.AppResult
import it.archesurvey.app.domain.usecase.CreateSurveyUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NewSurveyViewModel(
    private val projectId: String,
    private val createSurveyUseCase: CreateSurveyUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(NewSurveyUiState())
    val uiState: StateFlow<NewSurveyUiState> = _uiState.asStateFlow()

    fun onEvent(
        event: NewSurveyUiEvent,
        onSurveySaved: (String) -> Unit = {}
    ) {
        when (event) {
            is NewSurveyUiEvent.NameChanged -> updateState(name = event.value)
            is NewSurveyUiEvent.NotesChanged -> updateState(notes = event.value)
            NewSurveyUiEvent.Save -> saveSurvey(onSurveySaved)
        }
    }

    private fun updateState(
        name: String = _uiState.value.name,
        notes: String = _uiState.value.notes
    ) {
        _uiState.value = _uiState.value.copy(
            name = name,
            notes = notes,
            canSave = name.isNotBlank(),
            errorMessage = null
        )
    }

    private fun saveSurvey(onSurveySaved: (String) -> Unit) {
        val state = _uiState.value
        if (!state.canSave || state.isSaving) return

        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true, errorMessage = null)
            when (val result = createSurveyUseCase(
                projectId = projectId,
                title = state.name,
                notes = state.notes
            )) {
                is AppResult.Success -> onSurveySaved(result.value.id)
                is AppResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        errorMessage = result.reason.message
                    )
                }
            }
        }
    }

    class Factory(
        private val projectId: String,
        private val createSurveyUseCase: CreateSurveyUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return NewSurveyViewModel(
                projectId = projectId,
                createSurveyUseCase = createSurveyUseCase
            ) as T
        }
    }
}
