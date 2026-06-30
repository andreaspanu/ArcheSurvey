package it.archesurvey.app.features.projects

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import it.archesurvey.app.R
import it.archesurvey.app.core.designsystem.AppScreenPlaceholder

@Composable
fun ProjectsScreen(onBack: () -> Unit) {
    AppScreenPlaceholder(
        title = stringResource(R.string.projects_title),
        message = stringResource(R.string.projects_empty_state),
        onBack = onBack
    )
}
