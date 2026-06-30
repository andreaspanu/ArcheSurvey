package it.archesurvey.app.features.surveyworkspace

import it.archesurvey.app.domain.model.OpeningType

sealed interface SurveyWorkspaceUiEvent {
    data object AddCorner : SurveyWorkspaceUiEvent
    data object AddWall : SurveyWorkspaceUiEvent
    data object AddOpening : SurveyWorkspaceUiEvent
    data object AddManualMeasurement : SurveyWorkspaceUiEvent
    data class OpeningTypeSelected(val type: OpeningType) : SurveyWorkspaceUiEvent
    data class ArCoreStatusChanged(val status: String) : SurveyWorkspaceUiEvent
}
