package it.archesurvey.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import it.archesurvey.app.navigation.AppNavigation
import it.archesurvey.app.ui.theme.ArcheSurveyTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            ArcheSurveyTheme {
                AppNavigation()
            }
        }
    }
}
