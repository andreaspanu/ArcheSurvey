package it.archesurvey.app.features.surveyworkspace

import it.archesurvey.app.domain.model.OpeningType
import it.archesurvey.app.domain.model.SurveyWorkspace

data class SurveyWorkspaceUiState(
    val workspace: SurveyWorkspace,
    val selectedOpeningType: OpeningType = OpeningType.DOOR,
    val manualMeasurementDescription: String = "",
    val manualMeasurementValueMeters: String = "",
    val associateMeasurementToLastSegment: Boolean = true,
    val history: List<SurveyWorkspaceElementType> = emptyList(),
    val arCoreStatus: String = ""
)

enum class SurveyWorkspaceElementType {
    POINT,
    SEGMENT,
    OPENING,
    MEASUREMENT
}
