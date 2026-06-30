package it.archesurvey.app.features.surveyworkspace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import it.archesurvey.app.domain.model.ManualMeasurement
import it.archesurvey.app.domain.model.OpeningType
import it.archesurvey.app.domain.model.SurveyPoint
import it.archesurvey.app.domain.model.SurveySegment
import it.archesurvey.app.domain.model.SurveyWorkspace
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
            SurveyWorkspaceUiEvent.AddPoint -> addPoint()
            SurveyWorkspaceUiEvent.AddSegmentBetweenPoints -> addSegmentBetweenLastPoints()
            SurveyWorkspaceUiEvent.AddOpening -> addOpening()
            SurveyWorkspaceUiEvent.AddManualMeasurement -> addManualMeasurement()
            SurveyWorkspaceUiEvent.DeleteLastElement -> deleteLastElement()
            is SurveyWorkspaceUiEvent.ManualMeasurementDescriptionChanged -> {
                _uiState.value = _uiState.value.copy(manualMeasurementDescription = event.value)
            }
            is SurveyWorkspaceUiEvent.ManualMeasurementValueChanged -> {
                _uiState.value = _uiState.value.copy(manualMeasurementValueMeters = event.value)
            }
            is SurveyWorkspaceUiEvent.AssociateMeasurementToLastSegmentChanged -> {
                _uiState.value = _uiState.value.copy(associateMeasurementToLastSegment = event.enabled)
            }
            is SurveyWorkspaceUiEvent.OpeningTypeSelected -> {
                _uiState.value = _uiState.value.copy(selectedOpeningType = event.type)
            }
            is SurveyWorkspaceUiEvent.ArCoreStatusChanged -> {
                _uiState.value = _uiState.value.copy(arCoreStatus = event.status)
            }
        }
    }

    private fun addPoint() {
        val state = _uiState.value
        val index = state.workspace.points.size
        val point = SurveyPoint(
            id = newId(),
            xMeters = (index % GRID_COLUMNS).toFloat() * DEFAULT_POINT_STEP_METERS,
            yMeters = (index / GRID_COLUMNS).toFloat() * DEFAULT_POINT_STEP_METERS
        )
        _uiState.value = state.copy(
            workspace = state.workspace.copy(
                points = state.workspace.points + point
            ),
            history = state.history + SurveyWorkspaceElementType.POINT
        )
    }

    private fun addSegmentBetweenLastPoints() {
        val state = _uiState.value
        val points = state.workspace.points
        if (points.size < 2) {
            addPoint()
            addPoint()
        }
        val updatedState = _uiState.value
        val endPoint = updatedState.workspace.points.lastOrNull() ?: return
        val startPoint = updatedState.workspace.points.dropLast(1).lastOrNull() ?: return
        val exists = updatedState.workspace.segments.any {
            it.startPointId == startPoint.id && it.endPointId == endPoint.id
        }
        if (exists) return

        val segment = SurveySegment(
            id = newId(),
            startPointId = startPoint.id,
            endPointId = endPoint.id
        )
        _uiState.value = updatedState.copy(
            workspace = updatedState.workspace.copy(
                segments = updatedState.workspace.segments + segment
            ),
            history = updatedState.history + SurveyWorkspaceElementType.SEGMENT
        )
    }

    private fun addOpening() {
        ensureSegmentExists()
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
            ),
            history = state.history + SurveyWorkspaceElementType.OPENING
        )
    }

    private fun addManualMeasurement() {
        val state = _uiState.value
        val valueMeters = state.manualMeasurementValueMeters
            .replace(',', '.')
            .toFloatOrNull()
            ?: DEFAULT_MEASUREMENT_METERS
        val fallbackIndex = state.workspace.manualMeasurements.size + 1
        val measurement = ManualMeasurement(
            id = newId(),
            description = state.manualMeasurementDescription.ifBlank {
                "M$fallbackIndex"
            },
            valueMeters = valueMeters,
            segmentId = if (state.associateMeasurementToLastSegment) {
                state.workspace.segments.lastOrNull()?.id
            } else {
                null
            }
        )
        _uiState.value = state.copy(
            workspace = state.workspace.copy(
                manualMeasurements = state.workspace.manualMeasurements + measurement
            ),
            manualMeasurementDescription = "",
            manualMeasurementValueMeters = "",
            history = state.history + SurveyWorkspaceElementType.MEASUREMENT
        )
    }

    private fun deleteLastElement() {
        val state = _uiState.value
        val lastElement = state.history.lastOrNull() ?: return
        val workspace = state.workspace
        val updatedWorkspace = when (lastElement) {
            SurveyWorkspaceElementType.MEASUREMENT -> workspace.copy(
                manualMeasurements = workspace.manualMeasurements.dropLast(1)
            )
            SurveyWorkspaceElementType.OPENING -> workspace.copy(
                openings = workspace.openings.dropLast(1)
            )
            SurveyWorkspaceElementType.SEGMENT -> workspace.copy(
                segments = workspace.segments.dropLast(1)
            )
            SurveyWorkspaceElementType.POINT -> workspace.copy(
                points = workspace.points.dropLast(1)
            )
        }
        _uiState.value = state.copy(
            workspace = updatedWorkspace,
            history = state.history.dropLast(1)
        )
    }

    private fun ensureSegmentExists() {
        if (_uiState.value.workspace.segments.isEmpty()) {
            addSegmentBetweenLastPoints()
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

    private companion object {
        const val GRID_COLUMNS = 4
        const val DEFAULT_POINT_STEP_METERS = 2f
        const val DEFAULT_MEASUREMENT_METERS = 3f
    }
}
