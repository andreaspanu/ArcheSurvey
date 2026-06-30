package it.archesurvey.app.features.surveyworkspace.camera

data class CameraReticlePosition(
    val normalizedX: Float,
    val normalizedY: Float
)

interface CameraFeatureDetector {
    fun detectCandidate(reticlePosition: CameraReticlePosition): CameraReticlePosition
}

class ReticleCameraFeatureDetector : CameraFeatureDetector {
    override fun detectCandidate(reticlePosition: CameraReticlePosition): CameraReticlePosition {
        return reticlePosition
    }
}
