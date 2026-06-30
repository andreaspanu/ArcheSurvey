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
    data object SurveyWorkspace : Route("surveys/{surveyId}/workspace") {
        const val ARG_SURVEY_ID = "surveyId"

        fun create(surveyId: String): String {
            return "surveys/$surveyId/workspace"
        }
    }
    data object Settings : Route("settings")
    data object About : Route("about")
}
