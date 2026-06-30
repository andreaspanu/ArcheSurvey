package it.archesurvey.app.features.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import it.archesurvey.app.core.common.AppResult
import it.archesurvey.app.domain.usecase.DeleteProjectUseCase
import it.archesurvey.app.domain.usecase.GetProjectsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProjectsViewModel(
    private val getProjectsUseCase: GetProjectsUseCase,
    private val deleteProjectUseCase: DeleteProjectUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProjectsUiState())
    val uiState: StateFlow<ProjectsUiState> = _uiState.asStateFlow()

    init {
        loadProjects()
    }

    fun onEvent(event: ProjectsUiEvent) {
        when (event) {
            ProjectsUiEvent.Refresh -> loadProjects()
            is ProjectsUiEvent.RequestDeleteProject -> {
                _uiState.value = _uiState.value.copy(projectPendingDeletion = event.project)
            }
            ProjectsUiEvent.CancelDeleteProject -> {
                _uiState.value = _uiState.value.copy(projectPendingDeletion = null)
            }
            ProjectsUiEvent.ConfirmDeleteProject -> {
                val projectId = _uiState.value.projectPendingDeletion?.id ?: return
                deleteProject(projectId)
            }
        }
    }

    private fun loadProjects() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            _uiState.value = when (val result = getProjectsUseCase()) {
                is AppResult.Success -> ProjectsUiState(
                    isLoading = false,
                    projects = result.value
                )
                is AppResult.Error -> ProjectsUiState(
                    isLoading = false,
                    errorMessage = result.reason.message
                )
            }
        }
    }

    private fun deleteProject(projectId: String) {
        viewModelScope.launch {
            when (val result = deleteProjectUseCase(projectId)) {
                is AppResult.Success -> loadProjects()
                is AppResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        projectPendingDeletion = null,
                        errorMessage = result.reason.message
                    )
                }
            }
        }
    }

    class Factory(
        private val getProjectsUseCase: GetProjectsUseCase,
        private val deleteProjectUseCase: DeleteProjectUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProjectsViewModel(
                getProjectsUseCase = getProjectsUseCase,
                deleteProjectUseCase = deleteProjectUseCase
            ) as T
        }
    }
}
