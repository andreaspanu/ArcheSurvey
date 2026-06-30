package it.archesurvey.app.features.surveyworkspace

import it.archesurvey.app.domain.model.OpeningType
import it.archesurvey.app.features.surveyworkspace.ar.ArAvailabilityStatus
import it.archesurvey.app.features.surveyworkspace.ar.ArPointAcquisitionResult
import it.archesurvey.app.features.surveyworkspace.ar.ArTrackingState

sealed interface SurveyWorkspaceUiEvent {
    data object CapturePoint : SurveyWorkspaceUiEvent
    data object AddSegmentBetweenPoints : SurveyWorkspaceUiEvent
    data object AddOpening : SurveyWorkspaceUiEvent
    data object AddManualMeasurement : SurveyWorkspaceUiEvent
    data object DeleteLastElement : SurveyWorkspaceUiEvent
    data class ManualMeasurementDescriptionChanged(val value: String) : SurveyWorkspaceUiEvent
    data class ManualMeasurementValueChanged(val value: String) : SurveyWorkspaceUiEvent
    data class AssociateMeasurementToLastSegmentChanged(val enabled: Boolean) : SurveyWorkspaceUiEvent
    data class OpeningTypeSelected(val type: OpeningType) : SurveyWorkspaceUiEvent
    data class ArAvailabilityChanged(val status: ArAvailabilityStatus) : SurveyWorkspaceUiEvent
    data class ArTrackingStateChanged(val state: ArTrackingState) : SurveyWorkspaceUiEvent
    data class ArPointCandidateChanged(
        val result: ArPointAcquisitionResult
    ) : SurveyWorkspaceUiEvent
}
