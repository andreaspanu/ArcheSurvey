package it.archesurvey.app.domain.usecase

import it.archesurvey.app.core.common.AppResult
import it.archesurvey.app.domain.model.Survey
import it.archesurvey.app.domain.repository.SurveyRepository

class GetSurveysByProjectUseCase(
    private val surveyRepository: SurveyRepository
) {
    suspend operator fun invoke(projectId: String): AppResult<List<Survey>> {
        return surveyRepository.getSurveys(projectId)
    }
}
