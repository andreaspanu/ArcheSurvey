package it.archesurvey.app.features.surveyworkspace

import it.archesurvey.app.domain.model.SurveyWorkspace

data class SurveyWorkspaceUiState(
    val workspace: SurveyWorkspace,
    val selectedOpeningType: it.archesurvey.app.domain.model.OpeningType = it.archesurvey.app.domain.model.OpeningType.DOOR,
    val arCoreStatus: String = ""
)
