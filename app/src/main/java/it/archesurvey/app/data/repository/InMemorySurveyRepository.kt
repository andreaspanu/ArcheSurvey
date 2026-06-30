package it.archesurvey.app.data.repository

import it.archesurvey.app.core.common.AppResult
import it.archesurvey.app.domain.model.Survey
import it.archesurvey.app.domain.repository.SurveyRepository

class InMemorySurveyRepository : SurveyRepository {
    override suspend fun getSurveys(projectId: String): AppResult<List<Survey>> {
        return AppResult.Success(emptyList())
    }
}
