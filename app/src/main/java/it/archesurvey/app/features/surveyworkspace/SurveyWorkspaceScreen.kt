package it.archesurvey.app.features.surveyworkspace

import android.Manifest
import android.graphics.Paint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.ar.core.ArCoreApk
import it.archesurvey.app.R
import it.archesurvey.app.core.designsystem.AppButton
import it.archesurvey.app.core.designsystem.AppCard
import it.archesurvey.app.core.designsystem.AppTopBar
import it.archesurvey.app.core.designsystem.Spacing
import it.archesurvey.app.domain.model.OpeningType
import it.archesurvey.app.domain.model.SurveyPoint
import it.archesurvey.app.domain.model.SurveySegment
import it.archesurvey.app.domain.model.SurveyWorkspace
import kotlin.math.max
import kotlin.math.min

@Composable
fun SurveyWorkspaceScreen(
    uiState: SurveyWorkspaceUiState,
    onEvent: (SurveyWorkspaceUiEvent) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.survey_workspace_title),
                onBack = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(Spacing.large)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium)
        ) {
            CameraArWorkspaceCard(
                arCoreStatus = uiState.arCoreStatus,
                onArCoreStatusResolved = {
                    onEvent(SurveyWorkspaceUiEvent.ArCoreStatusChanged(it))
                }
            )
            ManualToolsCard(
                uiState = uiState,
                selectedOpeningType = uiState.selectedOpeningType,
                onEvent = onEvent
            )
            Preview2DCard(workspace = uiState.workspace)
        }
    }
}

@Composable
private fun CameraArWorkspaceCard(
    arCoreStatus: String,
    onArCoreStatusResolved: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(Unit) {
        val availability = ArCoreApk.getInstance().checkAvailability(context)
        onArCoreStatusResolved(availability.toString())
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    AppCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.survey_workspace_camera_title),
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = stringResource(R.string.survey_workspace_arcore_status, arCoreStatus),
            style = MaterialTheme.typography.bodyMedium
        )

        if (hasCameraPermission) {
            val cameraController = remember {
                LifecycleCameraController(context).apply {
                    cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                    bindToLifecycle(lifecycleOwner)
                }
            }

            DisposableEffect(cameraController) {
                onDispose {
                    cameraController.unbind()
                }
            }

            AndroidView(
                factory = { viewContext ->
                    PreviewView(viewContext).apply {
                        controller = cameraController
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f)
            )
        } else {
            Text(
                text = stringResource(R.string.survey_workspace_camera_permission),
                style = MaterialTheme.typography.bodyMedium
            )
            AppButton(
                text = stringResource(R.string.action_enable_camera),
                onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }
            )
        }
    }
}

@Composable
private fun ManualToolsCard(
    uiState: SurveyWorkspaceUiState,
    selectedOpeningType: OpeningType,
    onEvent: (SurveyWorkspaceUiEvent) -> Unit
) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.survey_workspace_manual_tools_title),
            style = MaterialTheme.typography.titleMedium
        )
        AppButton(
            text = stringResource(R.string.action_add_point),
            onClick = { onEvent(SurveyWorkspaceUiEvent.AddPoint) }
        )
        AppButton(
            text = stringResource(R.string.action_add_segment_between_points),
            onClick = { onEvent(SurveyWorkspaceUiEvent.AddSegmentBetweenPoints) }
        )
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.small)) {
            TextButton(
                onClick = {
                    onEvent(SurveyWorkspaceUiEvent.OpeningTypeSelected(OpeningType.DOOR))
                }
            ) {
                Text(text = stringResource(R.string.opening_type_door))
            }
            TextButton(
                onClick = {
                    onEvent(SurveyWorkspaceUiEvent.OpeningTypeSelected(OpeningType.WINDOW))
                }
            ) {
                Text(text = stringResource(R.string.opening_type_window))
            }
        }
        Text(
            text = stringResource(
                R.string.survey_workspace_selected_opening,
                selectedOpeningType.name
            ),
            style = MaterialTheme.typography.bodyMedium
        )
        AppButton(
            text = stringResource(R.string.action_add_opening),
            onClick = { onEvent(SurveyWorkspaceUiEvent.AddOpening) }
        )
        OutlinedTextField(
            value = uiState.manualMeasurementDescription,
            onValueChange = {
                onEvent(SurveyWorkspaceUiEvent.ManualMeasurementDescriptionChanged(it))
            },
            label = { Text(text = stringResource(R.string.manual_measurement_description_label)) },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = uiState.manualMeasurementValueMeters,
            onValueChange = {
                onEvent(SurveyWorkspaceUiEvent.ManualMeasurementValueChanged(it))
            },
            label = { Text(text = stringResource(R.string.manual_measurement_value_label)) },
            modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.small)) {
            Checkbox(
                checked = uiState.associateMeasurementToLastSegment,
                onCheckedChange = {
                    onEvent(SurveyWorkspaceUiEvent.AssociateMeasurementToLastSegmentChanged(it))
                }
            )
            Text(
                text = stringResource(R.string.manual_measurement_attach_last_segment),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        AppButton(
            text = stringResource(R.string.action_add_manual_measurement),
            onClick = { onEvent(SurveyWorkspaceUiEvent.AddManualMeasurement) }
        )
        AppButton(
            text = stringResource(R.string.action_delete_last_element),
            onClick = { onEvent(SurveyWorkspaceUiEvent.DeleteLastElement) },
            enabled = uiState.history.isNotEmpty()
        )
    }
}

@Composable
private fun Preview2DCard(workspace: SurveyWorkspace) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.survey_workspace_preview_title),
            style = MaterialTheme.typography.titleMedium
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.4f)
                .background(MaterialTheme.colorScheme.background)
        ) {
            WorkspaceCanvas(workspace = workspace)
        }
        MeasurementList(workspace = workspace)
        Text(
            text = stringResource(
                R.string.survey_workspace_summary,
                workspace.points.size,
                workspace.segments.size,
                workspace.openings.size,
                workspace.manualMeasurements.size
            ),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun WorkspaceCanvas(workspace: SurveyWorkspace) {
    val pointById = workspace.points.associateBy { it.id }
    Canvas(modifier = Modifier.fillMaxSize()) {
        val padding = 36.dp.toPx()
        val gridColor = Color(0xFFD7DEE8)
        val pointColor = Color(0xFFE67E22)
        val segmentColor = Color(0xFF16324F)
        val openingColor = Color(0xFF2E5E7E)
        val points = workspace.points
        val bounds = points.toBounds()
        val usableWidth = (size.width - padding * 2).coerceAtLeast(1f)
        val usableHeight = (size.height - padding * 2).coerceAtLeast(1f)
        val spanX = (bounds.maxX - bounds.minX).coerceAtLeast(1f)
        val spanY = (bounds.maxY - bounds.minY).coerceAtLeast(1f)
        val scale = min(usableWidth / spanX, usableHeight / spanY)

        fun map(point: SurveyPoint): Offset {
            val x = padding + (point.xMeters - bounds.minX) * scale
            val y = size.height - padding - (point.yMeters - bounds.minY) * scale
            return Offset(x, y)
        }

        val gridStep = 40.dp.toPx()
        var gridX = padding
        while (gridX <= size.width - padding) {
            drawLine(
                color = gridColor,
                start = Offset(gridX, padding),
                end = Offset(gridX, size.height - padding),
                strokeWidth = 1f
            )
            gridX += gridStep
        }
        var gridY = padding
        while (gridY <= size.height - padding) {
            drawLine(
                color = gridColor,
                start = Offset(padding, gridY),
                end = Offset(size.width - padding, gridY),
                strokeWidth = 1f
            )
            gridY += gridStep
        }

        workspace.segments.forEachIndexed { index, segment ->
            val start = pointById[segment.startPointId] ?: return@forEachIndexed
            val end = pointById[segment.endPointId] ?: return@forEachIndexed
            val startOffset = map(start)
            val endOffset = map(end)
            drawLine(
                color = segmentColor,
                start = startOffset,
                end = endOffset,
                strokeWidth = 5f
            )
            drawLabel(
                text = "S${index + 1}",
                position = Offset(
                    x = (startOffset.x + endOffset.x) / 2f,
                    y = (startOffset.y + endOffset.y) / 2f - 8.dp.toPx()
                )
            )
        }

        points.forEachIndexed { index, point ->
            val offset = map(point)
            drawCircle(
                color = pointColor,
                radius = 7.dp.toPx(),
                center = offset
            )
            drawLabel(
                text = "P${index + 1}",
                position = offset + Offset(8.dp.toPx(), -8.dp.toPx())
            )
        }

        workspace.openings.forEachIndexed { index, opening ->
            val segment = workspace.segments.firstOrNull { it.id == opening.segmentId }
                ?: return@forEachIndexed
            val start = pointById[segment.startPointId] ?: return@forEachIndexed
            val end = pointById[segment.endPointId] ?: return@forEachIndexed
            val startOffset = map(start)
            val endOffset = map(end)
            val center = Offset(
                x = (startOffset.x + endOffset.x) / 2f,
                y = (startOffset.y + endOffset.y) / 2f
            )
            drawCircle(
                color = openingColor,
                radius = 10.dp.toPx(),
                center = center,
                style = Stroke(width = 4f)
            )
            drawLabel(
                text = opening.type.name.take(1),
                position = center + Offset(8.dp.toPx(), 12.dp.toPx())
            )
            drawLabel(
                text = "A${index + 1}",
                position = center + Offset(-18.dp.toPx(), -12.dp.toPx())
            )
        }
    }
}

@Composable
private fun MeasurementList(workspace: SurveyWorkspace) {
    if (workspace.manualMeasurements.isEmpty()) return

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xSmall)) {
        workspace.manualMeasurements.forEachIndexed { index, measurement ->
            val segmentLabel = measurement.segmentId?.let {
                stringResource(R.string.manual_measurement_segment_attached)
            } ?: stringResource(R.string.manual_measurement_free)
            Text(
                text = stringResource(
                    R.string.manual_measurement_row,
                    index + 1,
                    measurement.description,
                    measurement.valueMeters,
                    segmentLabel
                ),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

private data class WorkspaceBounds(
    val minX: Float,
    val maxX: Float,
    val minY: Float,
    val maxY: Float
)

private fun List<SurveyPoint>.toBounds(): WorkspaceBounds {
    if (isEmpty()) {
        return WorkspaceBounds(
            minX = 0f,
            maxX = 4f,
            minY = 0f,
            maxY = 4f
        )
    }
    return WorkspaceBounds(
        minX = minOf { it.xMeters },
        maxX = maxOf { it.xMeters },
        minY = minOf { it.yMeters },
        maxY = maxOf { it.yMeters }
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawLabel(
    text: String,
    position: Offset
) {
    drawIntoCanvas { canvas ->
        val paint = Paint().apply {
            color = android.graphics.Color.rgb(22, 50, 79)
            textSize = 28f
            isAntiAlias = true
        }
        canvas.nativeCanvas.drawText(text, position.x, position.y, paint)
    }
}
