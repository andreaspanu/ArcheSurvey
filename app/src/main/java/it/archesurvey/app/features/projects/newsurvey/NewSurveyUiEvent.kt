package it.archesurvey.app.features.projects.newsurvey

sealed interface NewSurveyUiEvent {
    data class NameChanged(val value: String) : NewSurveyUiEvent
    data class NotesChanged(val value: String) : NewSurveyUiEvent
    data object Save : NewSurveyUiEvent
}
