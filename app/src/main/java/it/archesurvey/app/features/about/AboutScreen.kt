package it.archesurvey.app.features.about

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import it.archesurvey.app.R
import it.archesurvey.app.core.designsystem.AppScreenPlaceholder

@Composable
fun AboutScreen(onBack: () -> Unit) {
    AppScreenPlaceholder(
        title = stringResource(R.string.about_title),
        message = stringResource(R.string.about_description),
        onBack = onBack
    )
}
