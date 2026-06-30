package it.archesurvey.app.features.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "ARCHÈ SURVEY",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Smart Building Survey",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(40.dp))

        MenuButton("📐 Nuovo rilievo", onNewSurvey)
        MenuButton("📂 Progetti", onProjects)
        MenuButton("⚙️ Impostazioni", onSettings)
        MenuButton("ℹ️ Informazioni", onAbout)

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Versione 0.1.0",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun MenuButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(text)
    }
}