package it.archesurvey.app.domain.usecase

import it.archesurvey.app.core.common.AppResult
import it.archesurvey.app.domain.model.Project
import it.archesurvey.app.domain.repository.ProjectRepository

class GetProjectsUseCase(
    private val projectRepository: ProjectRepository
) {
    suspend operator fun invoke(): AppResult<List<Project>> {
        return projectRepository.getProjects()
    }
}
