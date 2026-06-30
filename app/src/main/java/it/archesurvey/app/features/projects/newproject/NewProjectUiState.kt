package it.archesurvey.app.features.projects.newproject

data class NewProjectUiState(
    val name: String = "",
    val client: String = "",
    val location: String = "",
    val notes: String = "",
    val isSaving: Boolean = false,
    val canSave: Boolean = false,
    val errorMessage: String? = null
)
