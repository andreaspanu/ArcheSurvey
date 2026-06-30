package it.archesurvey.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.compose.rememberNavController
import it.archesurvey.app.core.common.AppContainer
import it.archesurvey.app.features.about.AboutScreen
import it.archesurvey.app.features.about.AboutViewModel
import it.archesurvey.app.features.home.HomeScreen
import it.archesurvey.app.features.home.HomeUiEvent
import it.archesurvey.app.features.home.HomeViewModel
import it.archesurvey.app.features.projects.ProjectsScreen
import it.archesurvey.app.features.projects.ProjectsViewModel
import it.archesurvey.app.features.projects.detail.ProjectDetailScreen
import it.archesurvey.app.features.projects.detail.ProjectDetailViewModel
import it.archesurvey.app.features.projects.newproject.NewProjectScreen
import it.archesurvey.app.features.projects.newproject.NewProjectUiEvent
import it.archesurvey.app.features.projects.newproject.NewProjectViewModel
import it.archesurvey.app.features.projects.newsurvey.NewSurveyScreen
import it.archesurvey.app.features.projects.newsurvey.NewSurveyViewModel
import it.archesurvey.app.features.settings.SettingsScreen
import it.archesurvey.app.features.settings.SettingsViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val applicationContext = LocalContext.current.applicationContext
    val appContainer = remember(applicationContext) { AppContainer(applicationContext) }
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

    NavHost(
        navController = navController,
        startDestination = Route.Home.value
    ) {
        composable(Route.Home.value) { backStackEntry ->
            val viewModel: HomeViewModel = navViewModel(backStackEntry, defaultFactory)
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            HomeScreen(
                uiState = uiState,
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
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            ProjectsScreen(
                uiState = uiState,
                onEvent = viewModel::onEvent,
                onNewProject = { navController.navigate(Route.NewProject.value) },
                onProjectSelected = { projectId ->
                    navController.navigate(Route.ProjectDetail.create(projectId))
                },
                onBack = navController::navigateUp
            )
        }
        composable(Route.NewProject.value) { backStackEntry ->
            val viewModel: NewProjectViewModel = navViewModel(backStackEntry, newProjectFactory)
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
        composable(
            route = Route.ProjectDetail.value,
            arguments = listOf(
                navArgument(Route.ProjectDetail.ARG_PROJECT_ID) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString(Route.ProjectDetail.ARG_PROJECT_ID)
                ?: return@composable
            val projectDetailFactory = remember(projectId, appContainer) {
                ProjectDetailViewModel.Factory(
                    projectId = projectId,
                    getProjectUseCase = appContainer.getProjectUseCase,
                    getSurveysByProjectUseCase = appContainer.getSurveysByProjectUseCase
                )
            }
            val viewModel: ProjectDetailViewModel = navViewModel(
                backStackEntry = backStackEntry,
                factory = projectDetailFactory
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            ProjectDetailScreen(
                uiState = uiState,
                onEvent = viewModel::onEvent,
                onNewSurvey = { navController.navigate(Route.NewSurvey.create(projectId)) },
                onBack = navController::navigateUp
            )
        }
        composable(
            route = Route.NewSurvey.value,
            arguments = listOf(
                navArgument(Route.NewSurvey.ARG_PROJECT_ID) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString(Route.NewSurvey.ARG_PROJECT_ID)
                ?: return@composable
            val newSurveyFactory = remember(projectId, appContainer) {
                NewSurveyViewModel.Factory(
                    projectId = projectId,
                    createSurveyUseCase = appContainer.createSurveyUseCase
                )
            }
            val viewModel: NewSurveyViewModel = navViewModel(
                backStackEntry = backStackEntry,
                factory = newSurveyFactory
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            NewSurveyScreen(
                uiState = uiState,
                onEvent = { event ->
                    viewModel.onEvent(event) {
                        navController.navigate(Route.ProjectDetail.create(projectId)) {
                            popUpTo(Route.ProjectDetail.value) {
                                inclusive = true
                            }
                        }
                    }
                },
                onCancel = navController::navigateUp
            )
        }
        composable(Route.Settings.value) { backStackEntry ->
            val viewModel: SettingsViewModel = navViewModel(backStackEntry, defaultFactory)
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            SettingsScreen(
                uiState = uiState,
                onEvent = viewModel::onEvent,
                onBack = navController::navigateUp
            )
        }
        composable(Route.About.value) { backStackEntry ->
            val viewModel: AboutViewModel = navViewModel(backStackEntry, defaultFactory)
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            AboutScreen(
                uiState = uiState,
                onBack = navController::navigateUp
            )
        }
    }
}
