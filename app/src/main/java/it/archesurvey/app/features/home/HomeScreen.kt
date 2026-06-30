package it.archesurvey.app.features.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import it.archesurvey.app.R
import it.archesurvey.app.core.designsystem.AppButton
import it.archesurvey.app.core.designsystem.Spacing

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNewSurvey: () -> Unit = {},
    onProjects: () -> Unit = {},
    onSettings: () -> Unit = {},
    onAbout: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Spacing.large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.home_title),
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(Spacing.small))

        Text(
            text = stringResource(R.string.home_subtitle),
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(Spacing.xLarge))

        AppButton(stringResource(R.string.action_new_survey), onNewSurvey)
        AppButton(stringResource(R.string.action_projects), onProjects)
        AppButton(stringResource(R.string.action_settings), onSettings)
        AppButton(stringResource(R.string.action_about), onAbout)

        Spacer(modifier = Modifier.height(Spacing.xLarge))

        Text(
            text = stringResource(R.string.version_name),
            style = MaterialTheme.typography.bodySmall
        )
    }
}
