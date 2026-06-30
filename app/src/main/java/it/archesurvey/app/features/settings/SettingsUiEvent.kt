package it.archesurvey.app.features.settings

sealed interface SettingsUiEvent {
    data class OfflineModeChanged(val enabled: Boolean) : SettingsUiEvent
}
