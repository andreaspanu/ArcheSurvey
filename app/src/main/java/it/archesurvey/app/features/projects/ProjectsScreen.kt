package it.archesurvey.app.features.projects

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

@Composable
fun ProjectsScreen(
    uiState: ProjectsUiState,
    onEvent: (ProjectsUiEvent) -> Unit,
    onNewProject: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.projects_title),
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
            AppButton(
                text = stringResource(R.string.action_new_project),
                onClick = onNewProject
            )

            when {
                uiState.isLoading -> ProjectStatusCard(stringResource(R.string.projects_loading))
                uiState.errorMessage != null -> ProjectStatusCard(uiState.errorMessage)
                uiState.projects.isEmpty() -> EmptyProjectsCard()
                else -> {
                    Text(
                        text = stringResource(R.string.projects_count, uiState.projects.size),
                        style = MaterialTheme.typography.titleMedium
                    )
                    uiState.projects.forEach { project ->
                        ProjectCard(
                            project = project,
                            onDelete = {
                                onEvent(ProjectsUiEvent.DeleteProject(project.id))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyProjectsCard() {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.projects_empty_state),
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = stringResource(R.string.projects_empty_description),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ProjectStatusCard(message: String) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun ProjectCard(
    project: Project,
    onDelete: () -> Unit
) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = project.name,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = stringResource(R.string.project_client_value, project.client.ifBlank {
                stringResource(R.string.project_value_not_available)
            }),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = stringResource(R.string.project_location_value, project.location.ifBlank {
                stringResource(R.string.project_value_not_available)
            }),
            style = MaterialTheme.typography.bodyMedium
        )
        if (project.notes.isNotBlank()) {
            Text(
                text = project.notes,
                style = MaterialTheme.typography.bodySmall
            )
        }
        AppButton(
            text = stringResource(R.string.action_delete_project),
            onClick = onDelete
        )
    }
}
