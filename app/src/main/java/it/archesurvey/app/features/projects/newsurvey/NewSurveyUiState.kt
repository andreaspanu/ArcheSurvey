package it.archesurvey.app.features.projects.newsurvey

data class NewSurveyUiState(
    val name: String = "",
    val notes: String = "",
    val isSaving: Boolean = false,
    val canSave: Boolean = false,
    val errorMessage: String? = null
)
