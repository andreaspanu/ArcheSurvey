package it.archesurvey.app.features.home

sealed interface HomeUiEvent {
    data object NewSurveySelected : HomeUiEvent
    data object ProjectsSelected : HomeUiEvent
    data object SettingsSelected : HomeUiEvent
    data object AboutSelected : HomeUiEvent
}
