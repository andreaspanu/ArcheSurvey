package it.archesurvey.app.features.projects.newproject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import it.archesurvey.app.core.common.AppResult
import it.archesurvey.app.domain.usecase.CreateProjectUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NewProjectViewModel(
    private val createProjectUseCase: CreateProjectUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(NewProjectUiState())
    val uiState: StateFlow<NewProjectUiState> = _uiState.asStateFlow()

    fun onEvent(
        event: NewProjectUiEvent,
        onProjectSaved: () -> Unit = {}
    ) {
        when (event) {
            is NewProjectUiEvent.NameChanged -> updateState(name = event.value)
            is NewProjectUiEvent.ClientChanged -> updateState(client = event.value)
            is NewProjectUiEvent.LocationChanged -> updateState(location = event.value)
            is NewProjectUiEvent.NotesChanged -> updateState(notes = event.value)
            NewProjectUiEvent.Save -> saveProject(onProjectSaved)
        }
    }

    private fun updateState(
        name: String = _uiState.value.name,
        client: String = _uiState.value.client,
        location: String = _uiState.value.location,
        notes: String = _uiState.value.notes
    ) {
        _uiState.value = _uiState.value.copy(
            name = name,
            client = client,
            location = location,
            notes = notes,
            canSave = name.isNotBlank(),
            errorMessage = null
        )
    }

    private fun saveProject(onProjectSaved: () -> Unit) {
        val state = _uiState.value
        if (!state.canSave || state.isSaving) return

        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true, errorMessage = null)
            when (val result = createProjectUseCase(
                name = state.name,
                client = state.client,
                location = state.location,
                notes = state.notes
            )) {
                is AppResult.Success -> onProjectSaved()
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
        private val createProjectUseCase: CreateProjectUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return NewProjectViewModel(createProjectUseCase) as T
        }
    }
}
