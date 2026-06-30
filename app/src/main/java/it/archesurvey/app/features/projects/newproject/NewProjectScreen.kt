package it.archesurvey.app.features.projects.newproject

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
fun NewProjectScreen(
    uiState: NewProjectUiState,
    onEvent: (NewProjectUiEvent) -> Unit,
    onCancel: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.new_project_title),
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
                ProjectTextField(
                    value = uiState.name,
                    label = stringResource(R.string.project_name_label),
                    onValueChange = { onEvent(NewProjectUiEvent.NameChanged(it)) }
                )
                ProjectTextField(
                    value = uiState.client,
                    label = stringResource(R.string.project_client_label),
                    onValueChange = { onEvent(NewProjectUiEvent.ClientChanged(it)) }
                )
                ProjectTextField(
                    value = uiState.location,
                    label = stringResource(R.string.project_location_label),
                    onValueChange = { onEvent(NewProjectUiEvent.LocationChanged(it)) }
                )
                ProjectTextField(
                    value = uiState.notes,
                    label = stringResource(R.string.project_notes_label),
                    onValueChange = { onEvent(NewProjectUiEvent.NotesChanged(it)) },
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
                    stringResource(R.string.action_save_project)
                },
                onClick = { onEvent(NewProjectUiEvent.Save) },
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
private fun ProjectTextField(
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
