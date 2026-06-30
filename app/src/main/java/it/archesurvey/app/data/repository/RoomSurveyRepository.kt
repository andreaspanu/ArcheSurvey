package it.archesurvey.app.data.repository

import it.archesurvey.app.core.common.AppError
import it.archesurvey.app.core.common.AppResult
import it.archesurvey.app.data.local.dao.SurveyDao
import it.archesurvey.app.data.local.mapper.toDomain
import it.archesurvey.app.data.local.mapper.toEntity
import it.archesurvey.app.domain.model.Survey
import it.archesurvey.app.domain.repository.SurveyRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RoomSurveyRepository(
    private val surveyDao: SurveyDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SurveyRepository {
    override suspend fun getSurveys(projectId: String): AppResult<List<Survey>> {
        return withContext(ioDispatcher) {
            try {
                AppResult.Success(surveyDao.getSurveys(projectId).map { it.toDomain() })
            } catch (throwable: Throwable) {
                storageError(throwable)
            }
        }
    }

    override suspend fun addSurvey(survey: Survey): AppResult<Survey> {
        return withContext(ioDispatcher) {
            try {
                surveyDao.insertSurvey(survey.toEntity())
                AppResult.Success(survey)
            } catch (throwable: Throwable) {
                storageError(throwable)
            }
        }
    }

    override suspend fun deleteSurvey(surveyId: String): AppResult<Unit> {
        return withContext(ioDispatcher) {
            try {
                surveyDao.deleteSurvey(surveyId)
                AppResult.Success(Unit)
            } catch (throwable: Throwable) {
                storageError(throwable)
            }
        }
    }

    private fun storageError(throwable: Throwable): AppResult.Error {
        return AppResult.Error(
            AppError(
                code = "SURVEY_STORAGE_ERROR",
                message = throwable.message ?: "SURVEY_STORAGE_ERROR"
            )
        )
    }
}
