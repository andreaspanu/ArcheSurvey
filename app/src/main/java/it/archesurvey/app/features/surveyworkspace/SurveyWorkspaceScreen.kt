package it.archesurvey.app.features.surveyworkspace

import android.Manifest
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.drawscope.Stroke
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
import it.archesurvey.app.domain.model.SurveyWorkspace

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
    selectedOpeningType: OpeningType,
    onEvent: (SurveyWorkspaceUiEvent) -> Unit
) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.survey_workspace_manual_tools_title),
            style = MaterialTheme.typography.titleMedium
        )
        AppButton(
            text = stringResource(R.string.action_add_corner),
            onClick = { onEvent(SurveyWorkspaceUiEvent.AddCorner) }
        )
        AppButton(
            text = stringResource(R.string.action_add_wall),
            onClick = { onEvent(SurveyWorkspaceUiEvent.AddWall) }
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
        AppButton(
            text = stringResource(R.string.action_add_manual_measurement),
            onClick = { onEvent(SurveyWorkspaceUiEvent.AddManualMeasurement) }
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
            Canvas(modifier = Modifier.fillMaxSize()) {
                val padding = 32.dp.toPx()
                workspace.segments.forEachIndexed { index, _ ->
                    val y = padding + index * 36.dp.toPx()
                    drawLine(
                        color = Color(0xFF16324F),
                        start = Offset(padding, y),
                        end = Offset(size.width - padding, y),
                        strokeWidth = 5f
                    )
                }
                workspace.corners.forEachIndexed { index, _ ->
                    val x = padding + index * 28.dp.toPx()
                    drawCircle(
                        color = Color(0xFFE67E22),
                        radius = 8.dp.toPx(),
                        center = Offset(x.coerceAtMost(size.width - padding), padding)
                    )
                }
                workspace.openings.forEachIndexed { index, _ ->
                    val left = padding + index * 40.dp.toPx()
                    drawRect(
                        color = Color(0xFF2E5E7E),
                        topLeft = Offset(left, size.height / 2f),
                        size = androidx.compose.ui.geometry.Size(32.dp.toPx(), 16.dp.toPx()),
                        style = Stroke(width = 4f)
                    )
                }
            }
        }
        Text(
            text = stringResource(
                R.string.survey_workspace_summary,
                workspace.corners.size,
                workspace.segments.size,
                workspace.openings.size,
                workspace.manualMeasurements.size
            ),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
