package it.archesurvey.app.features.projects

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
fun ProjectsScreen(
    uiState: ProjectsUiState,
    onEvent: (ProjectsUiEvent) -> Unit,
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
                .padding(Spacing.large),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium)
        ) {
            val message = when {
                uiState.isLoading -> stringResource(R.string.projects_loading)
                uiState.errorMessage != null -> uiState.errorMessage
                uiState.projects.isEmpty() -> stringResource(R.string.projects_empty_state)
                else -> stringResource(R.string.projects_count, uiState.projects.size)
            }

            AppCard {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            AppButton(
                text = stringResource(R.string.action_refresh),
                onClick = { onEvent(ProjectsUiEvent.Refresh) }
            )
        }
    }
}
