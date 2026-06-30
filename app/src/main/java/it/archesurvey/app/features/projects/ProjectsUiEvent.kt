package it.archesurvey.app.features.projects

sealed interface ProjectsUiEvent {
    data object Refresh : ProjectsUiEvent
    data class DeleteProject(val projectId: String) : ProjectsUiEvent
}
