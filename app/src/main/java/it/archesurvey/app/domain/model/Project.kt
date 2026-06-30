package it.archesurvey.app.domain.model

data class Project(
    val id: String,
    val name: String,
    val client: String,
    val location: String,
    val notes: String
)
