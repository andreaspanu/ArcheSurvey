package it.archesurvey.app.features.surveyworkspace.ar

import com.google.ar.core.Pose
import com.google.ar.core.TrackingState

class ArCoordinateMapper {
    fun fromCameraPose(
        pose: Pose,
        trackingState: TrackingState
    ): ArPointAcquisitionResult {
        val mappedTrackingState = trackingState.toSurveyTrackingState()
        return ArPointAcquisitionResult(
            xMeters = pose.tx(),
            zMeters = pose.tz(),
            estimated = mappedTrackingState != ArTrackingState.TRACKING,
            trackingState = mappedTrackingState
        )
    }
}

fun TrackingState.toSurveyTrackingState(): ArTrackingState {
    return when (this) {
        TrackingState.TRACKING -> ArTrackingState.TRACKING
        TrackingState.PAUSED -> ArTrackingState.LIMITED
        TrackingState.STOPPED -> ArTrackingState.LOST
    }
}
