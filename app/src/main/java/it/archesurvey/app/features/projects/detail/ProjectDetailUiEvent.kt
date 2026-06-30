package it.archesurvey.app.features.projects.detail

sealed interface ProjectDetailUiEvent {
    data object Refresh : ProjectDetailUiEvent
}
