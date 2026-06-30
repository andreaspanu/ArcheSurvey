package it.archesurvey.app.features.surveyworkspace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import it.archesurvey.app.domain.model.ManualMeasurement
import it.archesurvey.app.domain.model.OpeningType
import it.archesurvey.app.domain.model.SurveyPoint
import it.archesurvey.app.domain.model.SurveySegment
import it.archesurvey.app.domain.model.SurveyWorkspace
import it.archesurvey.app.domain.model.WallCorner
import it.archesurvey.app.domain.model.WallOpening
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SurveyWorkspaceViewModel(
    surveyId: String
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        SurveyWorkspaceUiState(
            workspace = SurveyWorkspace(surveyId = surveyId)
        )
    )
    val uiState: StateFlow<SurveyWorkspaceUiState> = _uiState.asStateFlow()

    fun onEvent(event: SurveyWorkspaceUiEvent) {
        when (event) {
            SurveyWorkspaceUiEvent.AddCorner -> addCorner()
            SurveyWorkspaceUiEvent.AddWall -> addWall()
            SurveyWorkspaceUiEvent.AddOpening -> addOpening()
            SurveyWorkspaceUiEvent.AddManualMeasurement -> addManualMeasurement()
            is SurveyWorkspaceUiEvent.OpeningTypeSelected -> {
                _uiState.value = _uiState.value.copy(selectedOpeningType = event.type)
            }
            is SurveyWorkspaceUiEvent.ArCoreStatusChanged -> {
                _uiState.value = _uiState.value.copy(arCoreStatus = event.status)
            }
        }
    }

    private fun addCorner() {
        val state = _uiState.value
        val index = state.workspace.corners.size
        val point = SurveyPoint(
            id = newId(),
            xMeters = index.toFloat(),
            yMeters = if (index % 2 == 0) 0f else 3f
        )
        _uiState.value = state.copy(
            workspace = state.workspace.copy(
                points = state.workspace.points + point,
                corners = state.workspace.corners + WallCorner(
                    id = newId(),
                    point = point
                )
            )
        )
    }

    private fun addWall() {
        val state = _uiState.value
        val base = state.workspace.segments.size.toFloat()
        val start = SurveyPoint(newId(), base, 0f)
        val end = SurveyPoint(newId(), base + 2f, 0f)
        val segment = SurveySegment(
            id = newId(),
            startPointId = start.id,
            endPointId = end.id
        )
        _uiState.value = state.copy(
            workspace = state.workspace.copy(
                points = state.workspace.points + listOf(start, end),
                segments = state.workspace.segments + segment
            )
        )
    }

    private fun addOpening() {
        ensureWallExists()
        val state = _uiState.value
        val segment = state.workspace.segments.lastOrNull() ?: return
        val opening = WallOpening(
            id = newId(),
            segmentId = segment.id,
            type = state.selectedOpeningType,
            widthMeters = if (state.selectedOpeningType == OpeningType.DOOR) 0.9f else 1.2f,
            offsetMeters = 0.5f
        )
        _uiState.value = state.copy(
            workspace = state.workspace.copy(
                openings = state.workspace.openings + opening
            )
        )
    }

    private fun addManualMeasurement() {
        val state = _uiState.value
        val measurement = ManualMeasurement(
            id = newId(),
            label = "M${state.workspace.manualMeasurements.size + 1}",
            valueMeters = 3.0f
        )
        _uiState.value = state.copy(
            workspace = state.workspace.copy(
                manualMeasurements = state.workspace.manualMeasurements + measurement
            )
        )
    }

    private fun ensureWallExists() {
        if (_uiState.value.workspace.segments.isEmpty()) {
            addWall()
        }
    }

    private fun newId(): String = UUID.randomUUID().toString()

    class Factory(
        private val surveyId: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SurveyWorkspaceViewModel(surveyId) as T
        }
    }
}
