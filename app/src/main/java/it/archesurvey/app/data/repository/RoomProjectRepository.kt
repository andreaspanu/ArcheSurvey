package it.archesurvey.app.data.repository

import it.archesurvey.app.core.common.AppError
import it.archesurvey.app.core.common.AppResult
import it.archesurvey.app.data.local.dao.ProjectDao
import it.archesurvey.app.data.local.mapper.toDomain
import it.archesurvey.app.data.local.mapper.toEntity
import it.archesurvey.app.domain.model.Project
import it.archesurvey.app.domain.repository.ProjectRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RoomProjectRepository(
    private val projectDao: ProjectDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ProjectRepository {
    override suspend fun getProjects(): AppResult<List<Project>> {
        return withContext(ioDispatcher) {
            try {
                AppResult.Success(projectDao.getProjects().map { it.toDomain() })
            } catch (throwable: Throwable) {
                storageError(throwable)
            }
        }
    }

    override suspend fun getProject(projectId: String): AppResult<Project> {
        return withContext(ioDispatcher) {
            try {
                val project = projectDao.getProject(projectId)
                if (project != null) {
                    AppResult.Success(project.toDomain())
                } else {
                    AppResult.Error(
                        AppError(
                            code = "PROJECT_NOT_FOUND",
                            message = "PROJECT_NOT_FOUND"
                        )
                    )
                }
            } catch (throwable: Throwable) {
                storageError(throwable)
            }
        }
    }

    override suspend fun addProject(project: Project): AppResult<Project> {
        return withContext(ioDispatcher) {
            try {
                projectDao.insertProject(project.toEntity())
                AppResult.Success(project)
            } catch (throwable: Throwable) {
                storageError(throwable)
            }
        }
    }

    override suspend fun deleteProject(projectId: String): AppResult<Unit> {
        return withContext(ioDispatcher) {
            try {
                projectDao.deleteProject(projectId)
                AppResult.Success(Unit)
            } catch (throwable: Throwable) {
                storageError(throwable)
            }
        }
    }

    private fun storageError(throwable: Throwable): AppResult.Error {
        return AppResult.Error(
            AppError(
                code = "PROJECT_STORAGE_ERROR",
                message = throwable.message ?: "PROJECT_STORAGE_ERROR"
            )
        )
    }
}
