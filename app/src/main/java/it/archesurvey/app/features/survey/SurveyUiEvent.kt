package it.archesurvey.app.features.survey

sealed interface SurveyUiEvent {
    data object Refresh : SurveyUiEvent
    data object CaptureRequested : SurveyUiEvent
}
