package it.archesurvey.app.features.projects.detail

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
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
import it.archesurvey.app.domain.model.Project
import it.archesurvey.app.domain.model.Survey
import java.text.DateFormat
import java.util.Date

@Composable
fun ProjectDetailScreen(
    uiState: ProjectDetailUiState,
    onEvent: (ProjectDetailUiEvent) -> Unit,
    onNewSurvey: () -> Unit,
    onSurveySelected: (String) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.project_detail_title),
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
            when {
                uiState.isLoading -> StatusCard(stringResource(R.string.project_detail_loading))
                uiState.errorMessage != null -> StatusCard(uiState.errorMessage)
                uiState.project != null -> {
                    ProjectInfoCard(project = uiState.project)
                    AppButton(
                        text = stringResource(R.string.action_new_survey),
                        onClick = onNewSurvey
                    )
                    SurveyListCard(
                        surveys = uiState.surveys,
                        onSurveySelected = onSurveySelected
                    )
                    AppButton(
                        text = stringResource(R.string.action_refresh),
                        onClick = { onEvent(ProjectDetailUiEvent.Refresh) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProjectInfoCard(project: Project) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = project.name,
            style = MaterialTheme.typography.titleLarge
        )
        DetailLine(
            label = stringResource(R.string.project_client_label),
            value = project.client.ifBlank { stringResource(R.string.project_value_not_available) }
        )
        DetailLine(
            label = stringResource(R.string.project_location_label),
            value = project.location.ifBlank { stringResource(R.string.project_value_not_available) }
        )
        DetailLine(
            label = stringResource(R.string.project_created_at_label),
            value = project.createdAtMillis?.let { formatDate(it) }
                ?: stringResource(R.string.project_value_not_available)
        )
        if (project.notes.isNotBlank()) {
            DetailLine(
                label = stringResource(R.string.project_notes_label),
                value = project.notes
            )
        }
    }
}

@Composable
private fun SurveyListCard(
    surveys: List<Survey>,
    onSurveySelected: (String) -> Unit
) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.project_surveys_title),
            style = MaterialTheme.typography.titleMedium
        )
        if (surveys.isEmpty()) {
            Text(
                text = stringResource(R.string.project_surveys_empty_state),
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            surveys.forEach { survey ->
                AppButton(
                    text = survey.title,
                    onClick = { onSurveySelected(survey.id) }
                )
                if (survey.notes.isNotBlank()) {
                    Text(
                        text = survey.notes,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailLine(
    label: String,
    value: String
) {
    Text(
        text = stringResource(R.string.project_detail_line, label, value),
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
private fun StatusCard(message: String) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

private fun formatDate(timestamp: Long): String {
    return DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(timestamp))
}
