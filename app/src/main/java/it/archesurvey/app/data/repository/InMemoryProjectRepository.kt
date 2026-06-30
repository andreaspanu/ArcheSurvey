package it.archesurvey.app.data.repository

import it.archesurvey.app.core.common.AppResult
import it.archesurvey.app.core.common.AppError
import it.archesurvey.app.domain.model.Project
import it.archesurvey.app.domain.repository.ProjectRepository

class InMemoryProjectRepository : ProjectRepository {
    private val projects = mutableListOf<Project>()

    override suspend fun getProjects(): AppResult<List<Project>> {
        return AppResult.Success(projects.toList())
    }

    override suspend fun getProject(projectId: String): AppResult<Project> {
        val project = projects.firstOrNull { it.id == projectId }
        return if (project != null) {
            AppResult.Success(project)
        } else {
            AppResult.Error(
                AppError(
                    code = "PROJECT_NOT_FOUND",
                    message = "Project not found"
                )
            )
        }
    }

    override suspend fun addProject(project: Project): AppResult<Project> {
        projects.add(project)
        return AppResult.Success(project)
    }

    override suspend fun deleteProject(projectId: String): AppResult<Unit> {
        projects.removeAll { it.id == projectId }
        return AppResult.Success(Unit)
    }
}
