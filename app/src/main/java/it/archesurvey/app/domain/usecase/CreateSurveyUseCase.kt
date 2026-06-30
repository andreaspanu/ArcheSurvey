package it.archesurvey.app.domain.usecase

import it.archesurvey.app.core.common.AppError
import it.archesurvey.app.core.common.AppResult
import it.archesurvey.app.domain.model.Survey
import it.archesurvey.app.domain.repository.SurveyRepository

class CreateSurveyUseCase(
    private val surveyRepository: SurveyRepository
) {
    suspend operator fun invoke(
        projectId: String,
        title: String,
        notes: String
    ): AppResult<Survey> {
        val normalizedTitle = title.trim()
        if (normalizedTitle.isEmpty()) {
            return AppResult.Error(
                AppError(
                    code = "SURVEY_NAME_REQUIRED",
                    message = "Survey name is required"
                )
            )
        }

        val survey = Survey(
            id = "survey-${System.currentTimeMillis()}",
            projectId = projectId,
            title = normalizedTitle,
            notes = notes.trim(),
            createdAtMillis = System.currentTimeMillis()
        )

        return surveyRepository.addSurvey(survey)
    }
}
