package it.archesurvey.app.features.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import it.archesurvey.app.R
import it.archesurvey.app.core.designsystem.AppCard
import it.archesurvey.app.core.designsystem.AppTopBar
import it.archesurvey.app.core.designsystem.Spacing

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onEvent: (SettingsUiEvent) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.settings_title),
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
            AppCard {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
                ) {
                    Text(
                        text = stringResource(R.string.settings_offline_mode),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Switch(
                        checked = uiState.offlineModeEnabled,
                        onCheckedChange = {
                            onEvent(SettingsUiEvent.OfflineModeChanged(it))
                        }
                    )
                }
                Text(
                    text = if (uiState.offlineModeEnabled) {
                        stringResource(R.string.settings_offline_enabled)
                    } else {
                        stringResource(R.string.settings_offline_disabled)
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
