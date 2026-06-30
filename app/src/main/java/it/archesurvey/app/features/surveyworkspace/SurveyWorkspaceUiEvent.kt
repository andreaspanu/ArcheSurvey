package it.archesurvey.app.features.surveyworkspace

import it.archesurvey.app.domain.model.OpeningType

sealed interface SurveyWorkspaceUiEvent {
    data object AddPoint : SurveyWorkspaceUiEvent
    data object AddSegmentBetweenPoints : SurveyWorkspaceUiEvent
    data object AddOpening : SurveyWorkspaceUiEvent
    data object AddManualMeasurement : SurveyWorkspaceUiEvent
    data object DeleteLastElement : SurveyWorkspaceUiEvent
    data class ManualMeasurementDescriptionChanged(val value: String) : SurveyWorkspaceUiEvent
    data class ManualMeasurementValueChanged(val value: String) : SurveyWorkspaceUiEvent
    data class AssociateMeasurementToLastSegmentChanged(val enabled: Boolean) : SurveyWorkspaceUiEvent
    data class OpeningTypeSelected(val type: OpeningType) : SurveyWorkspaceUiEvent
    data class ArCoreStatusChanged(val status: String) : SurveyWorkspaceUiEvent
}
