package it.archesurvey.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import it.archesurvey.app.features.about.AboutScreen
import it.archesurvey.app.features.home.HomeScreen
import it.archesurvey.app.features.projects.ProjectsScreen
import it.archesurvey.app.features.settings.SettingsScreen
import it.archesurvey.app.features.survey.SurveyScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.Home.value
    ) {
        composable(Route.Home.value) {
            HomeScreen(
                onNewSurvey = { navController.navigate(Route.Survey.value) },
                onProjects = { navController.navigate(Route.Projects.value) },
                onSettings = { navController.navigate(Route.Settings.value) },
                onAbout = { navController.navigate(Route.About.value) }
            )
        }
        composable(Route.Projects.value) {
            ProjectsScreen(onBack = navController::navigateUp)
        }
        composable(Route.Survey.value) {
            SurveyScreen(onBack = navController::navigateUp)
        }
        composable(Route.Settings.value) {
            SettingsScreen(onBack = navController::navigateUp)
        }
        composable(Route.About.value) {
            AboutScreen(onBack = navController::navigateUp)
        }
    }
}
