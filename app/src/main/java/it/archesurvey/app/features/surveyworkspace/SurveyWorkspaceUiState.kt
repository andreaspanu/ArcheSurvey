package it.archesurvey.app.features.surveyworkspace

import it.archesurvey.app.domain.model.OpeningType
import it.archesurvey.app.domain.model.SurveyWorkspace
import it.archesurvey.app.features.surveyworkspace.ar.ArAvailabilityStatus
import it.archesurvey.app.features.surveyworkspace.ar.ArPointAcquisitionResult
import it.archesurvey.app.features.surveyworkspace.ar.ArTrackingState

data class SurveyWorkspaceUiState(
    val workspace: SurveyWorkspace,
    val selectedOpeningType: OpeningType = OpeningType.DOOR,
    val manualMeasurementDescription: String = "",
    val manualMeasurementValueMeters: String = "",
    val associateMeasurementToLastSegment: Boolean = true,
    val history: List<SurveyWorkspaceElementType> = emptyList(),
    val reticleNormalizedX: Float = 0.5f,
    val reticleNormalizedY: Float = 0.5f,
    val acquisitionStatus: SurveyAcquisitionStatus = SurveyAcquisitionStatus.CAMERA_READY,
    val arAvailabilityStatus: ArAvailabilityStatus = ArAvailabilityStatus.CHECKING,
    val arTrackingState: ArTrackingState = ArTrackingState.LOST,
    val arPointCandidate: ArPointAcquisitionResult? = null,
    val lastCapturedPointEstimated: Boolean = false,
    val scaleCorrectionFactor: Float? = null
)

enum class SurveyWorkspaceElementType {
    POINT,
    SEGMENT,
    OPENING,
    MEASUREMENT
}

enum class SurveyAcquisitionStatus {
    CAMERA_READY,
    POINT_ACQUIRED,
    SEGMENT_CREATED
}
