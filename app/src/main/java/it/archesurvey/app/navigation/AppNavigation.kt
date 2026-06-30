package it.archesurvey.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import it.archesurvey.app.core.common.AppContainer
import it.archesurvey.app.features.about.AboutScreen
import it.archesurvey.app.features.about.AboutViewModel
import it.archesurvey.app.features.home.HomeScreen
import it.archesurvey.app.features.home.HomeUiEvent
import it.archesurvey.app.features.home.HomeViewModel
import it.archesurvey.app.features.projects.ProjectsScreen
import it.archesurvey.app.features.projects.ProjectsViewModel
import it.archesurvey.app.features.projects.newproject.NewProjectScreen
import it.archesurvey.app.features.projects.newproject.NewProjectUiEvent
import it.archesurvey.app.features.projects.newproject.NewProjectViewModel
import it.archesurvey.app.features.settings.SettingsScreen
import it.archesurvey.app.features.settings.SettingsViewModel
import it.archesurvey.app.features.survey.SurveyScreen
import it.archesurvey.app.features.survey.SurveyViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val appContainer = remember { AppContainer() }
    val defaultFactory = remember { ViewModelProvider.NewInstanceFactory() }
    val projectsFactory = remember(appContainer) {
        ProjectsViewModel.Factory(
            getProjectsUseCase = appContainer.getProjectsUseCase,
            deleteProjectUseCase = appContainer.deleteProjectUseCase
        )
    }
    val newProjectFactory = remember(appContainer) {
        NewProjectViewModel.Factory(appContainer.createProjectUseCase)
    }
    val surveyFactory = remember(appContainer) {
        SurveyViewModel.Factory(appContainer.getSurveysUseCase)
    }

    NavHost(
        navController = navController,
        startDestination = Route.Home.value
    ) {
        composable(Route.Home.value) { backStackEntry ->
            val viewModel: HomeViewModel = navViewModel(backStackEntry, defaultFactory)
            val uiState by viewModel.uiState.collectAsState()

            HomeScreen(
                uiState = uiState,
                onNewSurvey = {
                    viewModel.onEvent(HomeUiEvent.NewSurveySelected)
                    navController.navigate(Route.Survey.value)
                },
                onProjects = {
                    viewModel.onEvent(HomeUiEvent.ProjectsSelected)
                    navController.navigate(Route.Projects.value)
                },
                onSettings = {
                    viewModel.onEvent(HomeUiEvent.SettingsSelected)
                    navController.navigate(Route.Settings.value)
                },
                onAbout = {
                    viewModel.onEvent(HomeUiEvent.AboutSelected)
                    navController.navigate(Route.About.value)
                }
            )
        }
        composable(Route.Projects.value) { backStackEntry ->
            val viewModel: ProjectsViewModel = navViewModel(backStackEntry, projectsFactory)
            val uiState by viewModel.uiState.collectAsState()

            ProjectsScreen(
                uiState = uiState,
                onEvent = viewModel::onEvent,
                onNewProject = { navController.navigate(Route.NewProject.value) },
                onBack = navController::navigateUp
            )
        }
        composable(Route.NewProject.value) { backStackEntry ->
            val viewModel: NewProjectViewModel = navViewModel(backStackEntry, newProjectFactory)
            val uiState by viewModel.uiState.collectAsState()

            NewProjectScreen(
                uiState = uiState,
                onEvent = { event ->
                    viewModel.onEvent(event) {
                        navController.navigate(Route.Projects.value) {
                            popUpTo(Route.Projects.value) {
                                inclusive = true
                            }
                        }
                    }
                },
                onCancel = navController::navigateUp
            )
        }
        composable(Route.Survey.value) { backStackEntry ->
            val viewModel: SurveyViewModel = navViewModel(backStackEntry, surveyFactory)
            val uiState by viewModel.uiState.collectAsState()

            SurveyScreen(
                uiState = uiState,
                onEvent = viewModel::onEvent,
                onBack = navController::navigateUp
            )
        }
        composable(Route.Settings.value) { backStackEntry ->
            val viewModel: SettingsViewModel = navViewModel(backStackEntry, defaultFactory)
            val uiState by viewModel.uiState.collectAsState()

            SettingsScreen(
                uiState = uiState,
                onEvent = viewModel::onEvent,
                onBack = navController::navigateUp
            )
        }
        composable(Route.About.value) { backStackEntry ->
            val viewModel: AboutViewModel = navViewModel(backStackEntry, defaultFactory)
            val uiState by viewModel.uiState.collectAsState()

            AboutScreen(
                uiState = uiState,
                onBack = navController::navigateUp
            )
        }
    }
}
