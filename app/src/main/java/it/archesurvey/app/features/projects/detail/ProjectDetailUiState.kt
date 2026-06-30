package it.archesurvey.app.features.projects.detail

import it.archesurvey.app.domain.model.Project
import it.archesurvey.app.domain.model.Survey

data class ProjectDetailUiState(
    val isLoading: Boolean = true,
    val project: Project? = null,
    val surveys: List<Survey> = emptyList(),
    val errorMessage: String? = null
)
