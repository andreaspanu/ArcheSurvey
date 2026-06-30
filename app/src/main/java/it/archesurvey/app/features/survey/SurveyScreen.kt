package it.archesurvey.app.features.survey

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import it.archesurvey.app.R
import it.archesurvey.app.core.designsystem.AppScreenPlaceholder

@Composable
fun SurveyScreen(onBack: () -> Unit) {
    AppScreenPlaceholder(
        title = stringResource(R.string.survey_title),
        message = stringResource(R.string.survey_empty_state),
        onBack = onBack
    )
}
