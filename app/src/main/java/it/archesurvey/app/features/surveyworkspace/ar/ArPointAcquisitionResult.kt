package it.archesurvey.app.features.surveyworkspace.ar

data class ArPointAcquisitionResult(
    val xMeters: Float,
    val zMeters: Float,
    val estimated: Boolean,
    val trackingState: ArTrackingState
)
