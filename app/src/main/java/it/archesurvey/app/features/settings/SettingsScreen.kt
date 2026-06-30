package it.archesurvey.app.features.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import it.archesurvey.app.R
import it.archesurvey.app.core.designsystem.AppScreenPlaceholder

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    AppScreenPlaceholder(
        title = stringResource(R.string.settings_title),
        message = stringResource(R.string.settings_empty_state),
        onBack = onBack
    )
}
