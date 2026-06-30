package it.archesurvey.app.features.survey

import it.archesurvey.app.domain.model.Survey

data class SurveyUiState(
    val isLoading: Boolean = true,
    val surveys: List<Survey> = emptyList(),
    val isCaptureReady: Boolean = false,
    val errorMessage: String? = null
)
