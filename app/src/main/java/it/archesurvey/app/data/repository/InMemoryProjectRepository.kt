package it.archesurvey.app.data.repository

import it.archesurvey.app.core.common.AppResult
import it.archesurvey.app.domain.model.Project
import it.archesurvey.app.domain.repository.ProjectRepository

class InMemoryProjectRepository : ProjectRepository {
    override suspend fun getProjects(): AppResult<List<Project>> {
        return AppResult.Success(emptyList())
    }
}
