package it.archesurvey.app.domain.model

data class SurveyWorkspace(
    val surveyId: String,
    val points: List<SurveyPoint> = emptyList(),
    val segments: List<SurveySegment> = emptyList(),
    val corners: List<WallCorner> = emptyList(),
    val openings: List<WallOpening> = emptyList(),
    val manualMeasurements: List<ManualMeasurement> = emptyList()
)
