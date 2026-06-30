package it.archesurvey.app.domain.repository

import it.archesurvey.app.core.common.AppResult
import it.archesurvey.app.domain.model.Project

interface ProjectRepository {
    suspend fun getProjects(): AppResult<List<Project>>
}
