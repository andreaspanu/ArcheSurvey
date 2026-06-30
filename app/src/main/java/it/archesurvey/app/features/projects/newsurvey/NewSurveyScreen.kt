package it.archesurvey.app.features.projects.newsurvey

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
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
fun NewSurveyScreen(
    uiState: NewSurveyUiState,
    onEvent: (NewSurveyUiEvent) -> Unit,
    onCancel: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.new_survey_title),
                onBack = onCancel
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
            AppCard(modifier = Modifier.fillMaxWidth()) {
                SurveyTextField(
                    value = uiState.name,
                    label = stringResource(R.string.survey_name_label),
                    onValueChange = { onEvent(NewSurveyUiEvent.NameChanged(it)) }
                )
                SurveyTextField(
                    value = uiState.notes,
                    label = stringResource(R.string.survey_notes_label),
                    onValueChange = { onEvent(NewSurveyUiEvent.NotesChanged(it)) },
                    minLines = 3
                )

                if (uiState.errorMessage != null) {
                    Text(text = uiState.errorMessage)
                }
            }

            AppButton(
                text = if (uiState.isSaving) {
                    stringResource(R.string.action_saving)
                } else {
                    stringResource(R.string.action_save_survey)
                },
                onClick = { onEvent(NewSurveyUiEvent.Save) },
                enabled = uiState.canSave && !uiState.isSaving
            )
            AppButton(
                text = stringResource(R.string.action_cancel),
                onClick = onCancel
            )
        }
    }
}

@Composable
private fun SurveyTextField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        minLines = minLines,
        modifier = Modifier.fillMaxWidth()
    )
}
