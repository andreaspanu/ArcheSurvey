package it.archesurvey.app.features.projects.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import it.archesurvey.app.core.common.AppResult
import it.archesurvey.app.domain.usecase.GetProjectUseCase
import it.archesurvey.app.domain.usecase.GetSurveysByProjectUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProjectDetailViewModel(
    private val projectId: String,
    private val getProjectUseCase: GetProjectUseCase,
    private val getSurveysByProjectUseCase: GetSurveysByProjectUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProjectDetailUiState())
    val uiState: StateFlow<ProjectDetailUiState> = _uiState.asStateFlow()

    init {
        loadProject()
    }

    fun onEvent(event: ProjectDetailUiEvent) {
        when (event) {
            ProjectDetailUiEvent.Refresh -> loadProject()
        }
    }

    private fun loadProject() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val projectResult = getProjectUseCase(projectId)
            val surveysResult = getSurveysByProjectUseCase(projectId)

            _uiState.value = when {
                projectResult is AppResult.Error -> ProjectDetailUiState(
                    isLoading = false,
                    errorMessage = projectResult.reason.message
                )
                surveysResult is AppResult.Error -> ProjectDetailUiState(
                    isLoading = false,
                    project = (projectResult as AppResult.Success).value,
                    errorMessage = surveysResult.reason.message
                )
                else -> ProjectDetailUiState(
                    isLoading = false,
                    project = (projectResult as AppResult.Success).value,
                    surveys = (surveysResult as AppResult.Success).value
                )
            }
        }
    }

    class Factory(
        private val projectId: String,
        private val getProjectUseCase: GetProjectUseCase,
        private val getSurveysByProjectUseCase: GetSurveysByProjectUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProjectDetailViewModel(
                projectId = projectId,
                getProjectUseCase = getProjectUseCase,
                getSurveysByProjectUseCase = getSurveysByProjectUseCase
            ) as T
        }
    }
}
