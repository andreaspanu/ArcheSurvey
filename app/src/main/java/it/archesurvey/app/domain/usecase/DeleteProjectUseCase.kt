package it.archesurvey.app.domain.usecase

import it.archesurvey.app.core.common.AppResult
import it.archesurvey.app.domain.repository.ProjectRepository

class DeleteProjectUseCase(
    private val projectRepository: ProjectRepository
) {
    suspend operator fun invoke(projectId: String): AppResult<Unit> {
        return projectRepository.deleteProject(projectId)
    }
}
