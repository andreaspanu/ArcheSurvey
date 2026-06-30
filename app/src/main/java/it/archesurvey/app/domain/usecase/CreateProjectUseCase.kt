package it.archesurvey.app.domain.usecase

import it.archesurvey.app.core.common.AppError
import it.archesurvey.app.core.common.AppResult
import it.archesurvey.app.domain.model.Project
import it.archesurvey.app.domain.repository.ProjectRepository

class CreateProjectUseCase(
    private val projectRepository: ProjectRepository
) {
    suspend operator fun invoke(
        name: String,
        client: String,
        location: String,
        notes: String
    ): AppResult<Project> {
        val normalizedName = name.trim()
        if (normalizedName.isEmpty()) {
            return AppResult.Error(
                AppError(
                    code = "PROJECT_NAME_REQUIRED",
                    message = "Project name is required"
                )
            )
        }

        val project = Project(
            id = "project-${System.currentTimeMillis()}",
            name = normalizedName,
            client = client.trim(),
            location = location.trim(),
            notes = notes.trim()
        )

        return projectRepository.addProject(project)
    }
}
