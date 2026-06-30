package it.archesurvey.app.domain.model

data class Survey(
    val id: String,
    val projectId: String,
    val title: String,
    val notes: String = "",
    val createdAtMillis: Long? = null
)
