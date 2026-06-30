package it.archesurvey.app.features.projects

import it.archesurvey.app.domain.model.Project

data class ProjectsUiState(
    val isLoading: Boolean = true,
    val projects: List<Project> = emptyList(),
    val projectPendingDeletion: Project? = null,
    val errorMessage: String? = null
)
