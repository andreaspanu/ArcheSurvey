package it.archesurvey.app.navigation

sealed class Route(val value: String) {
    data object Home : Route("home")
    data object Projects : Route("projects")
    data object Survey : Route("survey")
    data object Settings : Route("settings")
    data object About : Route("about")
}
