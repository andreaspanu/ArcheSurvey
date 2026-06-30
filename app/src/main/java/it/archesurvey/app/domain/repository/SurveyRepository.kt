package it.archesurvey.app.domain.repository

import it.archesurvey.app.core.common.AppResult
import it.archesurvey.app.domain.model.Survey

interface SurveyRepository {
    suspend fun getSurveys(projectId: String): AppResult<List<Survey>>
    suspend fun addSurvey(survey: Survey): AppResult<Survey>
    suspend fun deleteSurvey(surveyId: String): AppResult<Unit>
}
