package it.archesurvey.app.data.repository

import it.archesurvey.app.core.common.AppResult
import it.archesurvey.app.domain.model.Survey
import it.archesurvey.app.domain.repository.SurveyRepository

class InMemorySurveyRepository : SurveyRepository {
    private val surveys = mutableListOf<Survey>()

    override suspend fun getSurveys(projectId: String): AppResult<List<Survey>> {
        return AppResult.Success(surveys.filter { it.projectId == projectId })
    }

    override suspend fun addSurvey(survey: Survey): AppResult<Survey> {
        surveys.add(survey)
        return AppResult.Success(survey)
    }

    override suspend fun deleteSurvey(surveyId: String): AppResult<Unit> {
        surveys.removeAll { it.id == surveyId }
        return AppResult.Success(Unit)
    }
}
