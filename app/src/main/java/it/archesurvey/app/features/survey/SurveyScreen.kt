package it.archesurvey.app.features.survey

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import it.archesurvey.app.R
import it.archesurvey.app.core.designsystem.AppButton
import it.archesurvey.app.core.designsystem.AppCard
import it.archesurvey.app.core.designsystem.AppTopBar
import it.archesurvey.app.core.designsystem.Spacing

@Composable
fun SurveyScreen(
    uiState: SurveyUiState,
    onEvent: (SurveyUiEvent) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.survey_title),
                onBack = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(Spacing.large),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium)
        ) {
            val message = when {
                uiState.isLoading -> stringResource(R.string.survey_loading)
                uiState.errorMessage != null -> uiState.errorMessage
                uiState.surveys.isEmpty() -> stringResource(R.string.survey_empty_state)
                else -> stringResource(R.string.survey_count, uiState.surveys.size)
            }
            val captureStatus = if (uiState.isCaptureReady) {
                stringResource(R.string.survey_capture_ready)
            } else {
                stringResource(R.string.survey_capture_pending)
            }

            AppCard {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = captureStatus,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            AppButton(
                text = stringResource(R.string.action_prepare_capture),
                onClick = { onEvent(SurveyUiEvent.CaptureRequested) }
            )
            AppButton(
                text = stringResource(R.string.action_refresh),
                onClick = { onEvent(SurveyUiEvent.Refresh) }
            )
        }
    }
}
