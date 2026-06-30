package it.archesurvey.app.core.common

import it.archesurvey.app.data.repository.InMemoryProjectRepository
import it.archesurvey.app.data.repository.InMemorySurveyRepository
import it.archesurvey.app.domain.repository.ProjectRepository
import it.archesurvey.app.domain.repository.SurveyRepository
import it.archesurvey.app.domain.usecase.GetProjectsUseCase
import it.archesurvey.app.domain.usecase.GetSurveysUseCase

class AppContainer {
    private val projectRepository: ProjectRepository = InMemoryProjectRepository()
    private val surveyRepository: SurveyRepository = InMemorySurveyRepository()

    val getProjectsUseCase = GetProjectsUseCase(projectRepository)
    val getSurveysUseCase = GetSurveysUseCase(surveyRepository)
}
