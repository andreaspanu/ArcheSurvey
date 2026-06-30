package it.archesurvey.app.features.projects.newproject

sealed interface NewProjectUiEvent {
    data class NameChanged(val value: String) : NewProjectUiEvent
    data class ClientChanged(val value: String) : NewProjectUiEvent
    data class LocationChanged(val value: String) : NewProjectUiEvent
    data class NotesChanged(val value: String) : NewProjectUiEvent
    data object Save : NewProjectUiEvent
}
