package it.archesurvey.app.features.projects

sealed interface ProjectsUiEvent {
    data object Refresh : ProjectsUiEvent
}
