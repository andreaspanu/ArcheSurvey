package it.archesurvey.app.navigation

sealed class Route(val value: String) {
    data object Home : Route("home")
    data object Projects : Route("projects")
    data object NewProject : Route("projects/new")
    data object ProjectDetail : Route("projects/{projectId}") {
        const val ARG_PROJECT_ID = "projectId"

        fun create(projectId: String): String {
            return "projects/$projectId"
        }
    }
    data object NewSurvey : Route("projects/{projectId}/surveys/new") {
        const val ARG_PROJECT_ID = "projectId"

        fun create(projectId: String): String {
            return "projects/$projectId/surveys/new"
        }
    }
    data object Settings : Route("settings")
    data object About : Route("about")
}
