package it.archesurvey.app.domain.model

data class WallOpening(
    val id: String,
    val segmentId: String,
    val type: OpeningType,
    val widthMeters: Float,
    val offsetMeters: Float
)
