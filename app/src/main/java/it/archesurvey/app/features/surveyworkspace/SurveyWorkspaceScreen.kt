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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
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
import it.archesurvey.app.features.surveyworkspace.ar.ArAvailabilityStatus
import it.archesurvey.app.features.surveyworkspace.ar.ArSurveyPreviewView
import it.archesurvey.app.features.surveyworkspace.ar.ArTrackingState
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

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
            ArSurveyWorkspaceCard(
                uiState = uiState,
                onEvent = onEvent
            )
            ManualToolsCard(
                uiState = uiState,
                selectedOpeningType = uiState.selectedOpeningType,
                onEvent = onEvent
            )
            Preview2DCard(
                workspace = uiState.workspace,
                scaleCorrectionFactor = uiState.scaleCorrectionFactor
            )
        }
    }
}

@Composable
private fun ArSurveyWorkspaceCard(
    uiState: SurveyWorkspaceUiState,
    onEvent: (SurveyWorkspaceUiEvent) -> Unit
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
    var arPreviewView by remember {
        mutableStateOf<ArSurveyPreviewView?>(null)
    }

    LaunchedEffect(Unit) {
        val availability = ArCoreApk.getInstance().checkAvailability(context)
        onEvent(SurveyWorkspaceUiEvent.ArAvailabilityChanged(availability.toSurveyAvailability()))
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            arPreviewView?.pauseSession()
        }
    }

    DisposableEffect(lifecycleOwner, arPreviewView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> arPreviewView?.resumeSession()
                Lifecycle.Event.ON_PAUSE -> arPreviewView?.pauseSession()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    AppCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.ar_survey_title),
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = stringResource(R.string.ar_survey_practical_flow),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = stringResource(
                R.string.ar_survey_availability,
                arAvailabilityLabel(uiState.arAvailabilityStatus)
            ),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = stringResource(
                R.string.ar_survey_tracking,
                arTrackingLabel(uiState.arTrackingState)
            ),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = stringResource(
                R.string.survey_workspace_acquisition_status,
                acquisitionStatusLabel(uiState.acquisitionStatus)
            ),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = stringResource(
                R.string.survey_workspace_counters,
                uiState.workspace.points.size,
                uiState.workspace.segments.size,
                uiState.workspace.manualMeasurements.size
            ),
            style = MaterialTheme.typography.bodyMedium
        )

        if (hasCameraPermission) {
            val reticleColor = MaterialTheme.colorScheme.tertiary
            if (uiState.arAvailabilityStatus == ArAvailabilityStatus.AVAILABLE) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4f / 3f)
                ) {
                    AndroidView(
                        factory = { viewContext ->
                            ArSurveyPreviewView(viewContext).apply {
                                onTrackingStateChanged = {
                                    onEvent(SurveyWorkspaceUiEvent.ArTrackingStateChanged(it))
                                }
                                onPointCandidateChanged = {
                                    onEvent(SurveyWorkspaceUiEvent.ArPointCandidateChanged(it))
                                }
                                onSessionError = {
                                    onEvent(SurveyWorkspaceUiEvent.ArAvailabilityChanged(it))
                                }
                                resumeSession()
                                arPreviewView = this
                            }
                        },
                        update = { view ->
                            view.onTrackingStateChanged = {
                                onEvent(SurveyWorkspaceUiEvent.ArTrackingStateChanged(it))
                            }
                            view.onPointCandidateChanged = {
                                onEvent(SurveyWorkspaceUiEvent.ArPointCandidateChanged(it))
                            }
                            view.onSessionError = {
                                onEvent(SurveyWorkspaceUiEvent.ArAvailabilityChanged(it))
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                    CameraReticleOverlay(
                        color = reticleColor,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else {
                Text(
                    text = arFallbackMessage(uiState.arAvailabilityStatus),
                    style = MaterialTheme.typography.bodyMedium
                )
                CameraXFallbackPreview(
                    lifecycleOwner = lifecycleOwner,
                    reticleColor = reticleColor
                )
            }
            AppButton(
                text = stringResource(R.string.action_capture_point),
                onClick = { onEvent(SurveyWorkspaceUiEvent.CapturePoint) }
            )
            if (uiState.lastCapturedPointEstimated) {
                Text(
                    text = stringResource(R.string.ar_survey_last_point_estimated),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
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
private fun CameraXFallbackPreview(
    lifecycleOwner: LifecycleOwner,
    reticleColor: Color
) {
    val context = LocalContext.current
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

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(4f / 3f)
    ) {
        AndroidView(
            factory = { viewContext ->
                PreviewView(viewContext).apply {
                    controller = cameraController
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        CameraReticleOverlay(
            color = reticleColor,
            modifier = Modifier.fillMaxSize()
        )
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
private fun Preview2DCard(
    workspace: SurveyWorkspace,
    scaleCorrectionFactor: Float?
) {
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
            WorkspaceCanvas(
                workspace = workspace,
                scaleCorrectionFactor = scaleCorrectionFactor
            )
        }
        scaleCorrectionFactor?.let { factor ->
            Text(
                text = stringResource(R.string.ar_survey_geometry_calibrated, factor),
                style = MaterialTheme.typography.bodyMedium
            )
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
private fun WorkspaceCanvas(
    workspace: SurveyWorkspace,
    scaleCorrectionFactor: Float?
) {
    val pointById = workspace.points.associateBy { it.id }
    val segmentMeasurementLabels = workspace.manualMeasurements.mapNotNull { measurement ->
        measurement.segmentId?.let { segmentId ->
            segmentId to stringResource(
                R.string.survey_workspace_segment_measurement_label,
                measurement.valueMeters
            )
        }
    }.toMap()
    val segmentDistanceLabels = workspace.segments.mapNotNull { segment ->
        val distance = workspace.relativeDistanceMeters(segment) ?: return@mapNotNull null
        val correctedDistance = scaleCorrectionFactor?.let { distance * it }
        segment.id to SegmentDistanceLabels(
            estimatedLabel = stringResource(
                R.string.ar_survey_estimated_distance,
                distance
            ),
            correctedLabel = correctedDistance?.let {
                stringResource(R.string.ar_survey_corrected_distance, it)
            }
        )
    }.toMap()
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
            val visibleEndOffset = if (startOffset == endOffset) {
                startOffset + Offset(18.dp.toPx(), 0f)
            } else {
                endOffset
            }
            val segmentLabelPosition = Offset(
                x = (startOffset.x + visibleEndOffset.x) / 2f,
                y = (startOffset.y + visibleEndOffset.y) / 2f - 8.dp.toPx()
            )
            drawLine(
                color = segmentColor,
                start = startOffset,
                end = visibleEndOffset,
                strokeWidth = 5f
            )
            drawLabel(
                text = "S${index + 1}",
                position = segmentLabelPosition
            )
            segmentMeasurementLabels[segment.id]?.let { measurementLabel ->
                drawLabel(
                    text = measurementLabel,
                    position = segmentLabelPosition + Offset(0f, 24.dp.toPx())
                )
            }
            segmentDistanceLabels[segment.id]?.let { labels ->
                drawLabel(
                    text = labels.estimatedLabel,
                    position = segmentLabelPosition + Offset(0f, 48.dp.toPx())
                )
                labels.correctedLabel?.let { correctedLabel ->
                    drawLabel(
                        text = correctedLabel,
                        position = segmentLabelPosition + Offset(0f, 72.dp.toPx())
                    )
                }
            }
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

@Composable
private fun acquisitionStatusLabel(status: SurveyAcquisitionStatus): String {
    return when (status) {
        SurveyAcquisitionStatus.CAMERA_READY -> {
            stringResource(R.string.survey_workspace_status_camera_ready)
        }
        SurveyAcquisitionStatus.POINT_ACQUIRED -> {
            stringResource(R.string.survey_workspace_status_point_acquired)
        }
        SurveyAcquisitionStatus.SEGMENT_CREATED -> {
            stringResource(R.string.survey_workspace_status_segment_created)
        }
    }
}

@Composable
private fun CameraReticleOverlay(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val lineLength = 28.dp.toPx()
        val gap = 8.dp.toPx()
        drawCircle(
            color = color,
            radius = 18.dp.toPx(),
            center = center,
            style = Stroke(width = 3.dp.toPx())
        )
        drawLine(
            color = color,
            start = Offset(center.x - lineLength, center.y),
            end = Offset(center.x - gap, center.y),
            strokeWidth = 3.dp.toPx()
        )
        drawLine(
            color = color,
            start = Offset(center.x + gap, center.y),
            end = Offset(center.x + lineLength, center.y),
            strokeWidth = 3.dp.toPx()
        )
        drawLine(
            color = color,
            start = Offset(center.x, center.y - lineLength),
            end = Offset(center.x, center.y - gap),
            strokeWidth = 3.dp.toPx()
        )
        drawLine(
            color = color,
            start = Offset(center.x, center.y + gap),
            end = Offset(center.x, center.y + lineLength),
            strokeWidth = 3.dp.toPx()
        )
    }
}

@Composable
private fun arAvailabilityLabel(status: ArAvailabilityStatus): String {
    return when (status) {
        ArAvailabilityStatus.CHECKING -> {
            stringResource(R.string.ar_survey_availability_checking)
        }
        ArAvailabilityStatus.AVAILABLE -> {
            stringResource(R.string.ar_survey_availability_available)
        }
        ArAvailabilityStatus.NEEDS_INSTALL_OR_UPDATE -> {
            stringResource(R.string.ar_survey_availability_needs_install)
        }
        ArAvailabilityStatus.UNAVAILABLE -> {
            stringResource(R.string.ar_survey_availability_unavailable)
        }
    }
}

@Composable
private fun arTrackingLabel(state: ArTrackingState): String {
    return when (state) {
        ArTrackingState.TRACKING -> {
            stringResource(R.string.ar_survey_tracking_active)
        }
        ArTrackingState.LIMITED -> {
            stringResource(R.string.ar_survey_tracking_limited)
        }
        ArTrackingState.LOST -> {
            stringResource(R.string.ar_survey_tracking_lost)
        }
    }
}

@Composable
private fun arFallbackMessage(status: ArAvailabilityStatus): String {
    return when (status) {
        ArAvailabilityStatus.CHECKING -> {
            stringResource(R.string.ar_survey_fallback_checking)
        }
        ArAvailabilityStatus.AVAILABLE -> {
            stringResource(R.string.ar_survey_fallback_camera)
        }
        ArAvailabilityStatus.NEEDS_INSTALL_OR_UPDATE -> {
            stringResource(R.string.ar_survey_fallback_needs_install)
        }
        ArAvailabilityStatus.UNAVAILABLE -> {
            stringResource(R.string.ar_survey_fallback_unavailable)
        }
    }
}

private fun ArCoreApk.Availability.toSurveyAvailability(): ArAvailabilityStatus {
    val availabilityName = name
    return when {
        availabilityName == "SUPPORTED_INSTALLED" -> ArAvailabilityStatus.AVAILABLE
        availabilityName.startsWith("SUPPORTED_") -> {
            ArAvailabilityStatus.NEEDS_INSTALL_OR_UPDATE
        }
        availabilityName == "UNSUPPORTED_DEVICE_NOT_CAPABLE" -> {
            ArAvailabilityStatus.UNAVAILABLE
        }
        else -> ArAvailabilityStatus.CHECKING
    }
}

private data class SegmentDistanceLabels(
    val estimatedLabel: String,
    val correctedLabel: String?
)

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

private fun SurveyWorkspace.relativeDistanceMeters(segment: SurveySegment): Float? {
    val start = points.firstOrNull { it.id == segment.startPointId } ?: return null
    val end = points.firstOrNull { it.id == segment.endPointId } ?: return null
    val deltaX = end.xMeters - start.xMeters
    val deltaY = end.yMeters - start.yMeters
    return sqrt(deltaX * deltaX + deltaY * deltaY)
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
