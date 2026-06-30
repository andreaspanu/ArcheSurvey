package it.archesurvey.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavBackStackEntry

@Composable
inline fun <reified VM : ViewModel> navViewModel(
    backStackEntry: NavBackStackEntry,
    factory: ViewModelProvider.Factory
): VM {
    return remember(backStackEntry, factory) {
        ViewModelProvider(backStackEntry, factory)[VM::class.java]
    }
}
