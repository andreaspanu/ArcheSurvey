package it.archesurvey.app.core.common

import android.content.Context
import androidx.room.Room
import it.archesurvey.app.data.local.ArcheSurveyDatabase
import it.archesurvey.app.data.repository.RoomProjectRepository
import it.archesurvey.app.data.repository.InMemorySurveyRepository
import it.archesurvey.app.domain.repository.ProjectRepository
import it.archesurvey.app.domain.repository.SurveyRepository
import it.archesurvey.app.domain.usecase.CreateProjectUseCase
import it.archesurvey.app.domain.usecase.CreateSurveyUseCase
import it.archesurvey.app.domain.usecase.DeleteProjectUseCase
import it.archesurvey.app.domain.usecase.GetProjectUseCase
import it.archesurvey.app.domain.usecase.GetProjectsUseCase
import it.archesurvey.app.domain.usecase.GetSurveysByProjectUseCase
import it.archesurvey.app.domain.usecase.GetSurveysUseCase

class AppContainer(context: Context) {
    private val database = Room.databaseBuilder(
        context = context,
        klass = ArcheSurveyDatabase::class.java,
        name = DATABASE_NAME
    ).addMigrations(ArcheSurveyDatabase.MIGRATION_1_2).build()

    private val projectRepository: ProjectRepository = RoomProjectRepository(database.projectDao())
    private val surveyRepository: SurveyRepository = InMemorySurveyRepository()

    val getProjectsUseCase = GetProjectsUseCase(projectRepository)
    val getProjectUseCase = GetProjectUseCase(projectRepository)
    val createProjectUseCase = CreateProjectUseCase(projectRepository)
    val deleteProjectUseCase = DeleteProjectUseCase(projectRepository)
    val getSurveysUseCase = GetSurveysUseCase(surveyRepository)
    val getSurveysByProjectUseCase = GetSurveysByProjectUseCase(surveyRepository)
    val createSurveyUseCase = CreateSurveyUseCase(surveyRepository)

    private companion object {
        const val DATABASE_NAME = "archesurvey.db"
    }
}
