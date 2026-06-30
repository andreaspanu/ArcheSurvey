package it.archesurvey.app.domain.model

data class ManualMeasurement(
    val id: String,
    val description: String,
    val valueMeters: Float,
    val segmentId: String? = null
)
