package it.archesurvey.app.features.projects

sealed interface ProjectsUiEvent {
    data object Refresh : ProjectsUiEvent
    data class RequestDeleteProject(val project: it.archesurvey.app.domain.model.Project) : ProjectsUiEvent
    data object CancelDeleteProject : ProjectsUiEvent
    data object ConfirmDeleteProject : ProjectsUiEvent
}
