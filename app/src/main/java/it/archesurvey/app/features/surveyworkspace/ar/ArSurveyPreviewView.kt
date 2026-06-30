package it.archesurvey.app.features.surveyworkspace.ar

import android.content.Context
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.Session
import com.google.ar.core.TrackingState
import com.google.ar.core.exceptions.CameraNotAvailableException
import com.google.ar.core.exceptions.UnavailableApkTooOldException
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException
import com.google.ar.core.exceptions.UnavailableSdkTooOldException
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class ArSurveyPreviewView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : GLSurfaceView(context, attrs), GLSurfaceView.Renderer {
    var onTrackingStateChanged: ((ArTrackingState) -> Unit)? = null
    var onPointCandidateChanged: ((ArPointAcquisitionResult) -> Unit)? = null
    var onSessionError: ((ArAvailabilityStatus) -> Unit)? = null

    private val mainHandler = Handler(Looper.getMainLooper())
    private val coordinateMapper = ArCoordinateMapper()
    private var session: Session? = null
    private var textureId = 0
    private var programId = 0
    private var positionHandle = 0
    private var textureCoordinateHandle = 0
    private var textureUniformHandle = 0
    private var cameraTextureConfigured = false

    private val quadVertices = floatBufferOf(
        -1f, -1f,
        1f, -1f,
        -1f, 1f,
        1f, 1f
    )
    private val textureCoordinates = floatBufferOf(
        0f, 1f,
        1f, 1f,
        0f, 0f,
        1f, 0f
    )

    init {
        setEGLContextClientVersion(2)
        preserveEGLContextOnPause = true
        setRenderer(this)
        renderMode = RENDERMODE_CONTINUOUSLY
    }

    fun resumeSession() {
        super.onResume()
        if (session != null) {
            tryResumeSession()
            return
        }

        try {
            session = Session(context).also { arSession ->
                val config = Config(arSession).apply {
                    planeFindingMode = Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL
                    updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
                    focusMode = Config.FocusMode.AUTO
                }
                arSession.configure(config)
            }
            tryResumeSession()
        } catch (_: UnavailableArcoreNotInstalledException) {
            reportSessionError(ArAvailabilityStatus.NEEDS_INSTALL_OR_UPDATE)
        } catch (_: UnavailableApkTooOldException) {
            reportSessionError(ArAvailabilityStatus.NEEDS_INSTALL_OR_UPDATE)
        } catch (_: UnavailableSdkTooOldException) {
            reportSessionError(ArAvailabilityStatus.NEEDS_INSTALL_OR_UPDATE)
        } catch (_: UnavailableUserDeclinedInstallationException) {
            reportSessionError(ArAvailabilityStatus.NEEDS_INSTALL_OR_UPDATE)
        } catch (_: UnavailableDeviceNotCompatibleException) {
            reportSessionError(ArAvailabilityStatus.UNAVAILABLE)
        } catch (_: SecurityException) {
            reportSessionError(ArAvailabilityStatus.UNAVAILABLE)
        }
    }

    fun pauseSession() {
        session?.pause()
        super.onPause()
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)
        textureId = createExternalTexture()
        programId = createProgram(VERTEX_SHADER, FRAGMENT_SHADER)
        positionHandle = GLES20.glGetAttribLocation(programId, "a_Position")
        textureCoordinateHandle = GLES20.glGetAttribLocation(programId, "a_TexCoord")
        textureUniformHandle = GLES20.glGetUniformLocation(programId, "sTexture")
        cameraTextureConfigured = false
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        val arSession = session ?: return
        if (textureId == 0) return

        if (!cameraTextureConfigured) {
            arSession.setCameraTextureName(textureId)
            cameraTextureConfigured = true
        }

        try {
            val frame = arSession.update()
            drawCameraBackground()
            reportFrame(frame)
        } catch (_: CameraNotAvailableException) {
            reportTrackingState(ArTrackingState.LOST)
        } catch (_: RuntimeException) {
            reportTrackingState(ArTrackingState.LOST)
        }
    }

    private fun tryResumeSession() {
        try {
            session?.resume()
        } catch (_: CameraNotAvailableException) {
            reportSessionError(ArAvailabilityStatus.UNAVAILABLE)
        }
    }

    private fun reportFrame(frame: Frame) {
        val camera = frame.camera
        val result = coordinateMapper.fromCameraPose(
            pose = camera.pose,
            trackingState = camera.trackingState
        )
        reportTrackingState(result.trackingState)
        postToMain {
            onPointCandidateChanged?.invoke(result)
        }
    }

    private fun reportTrackingState(trackingState: ArTrackingState) {
        postToMain {
            onTrackingStateChanged?.invoke(trackingState)
        }
    }

    private fun reportSessionError(status: ArAvailabilityStatus) {
        postToMain {
            onSessionError?.invoke(status)
        }
    }

    private fun drawCameraBackground() {
        GLES20.glDisable(GLES20.GL_DEPTH_TEST)
        GLES20.glUseProgram(programId)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId)
        GLES20.glUniform1i(textureUniformHandle, 0)

        quadVertices.position(0)
        GLES20.glVertexAttribPointer(
            positionHandle,
            2,
            GLES20.GL_FLOAT,
            false,
            0,
            quadVertices
        )
        GLES20.glEnableVertexAttribArray(positionHandle)

        textureCoordinates.position(0)
        GLES20.glVertexAttribPointer(
            textureCoordinateHandle,
            2,
            GLES20.GL_FLOAT,
            false,
            0,
            textureCoordinates
        )
        GLES20.glEnableVertexAttribArray(textureCoordinateHandle)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(textureCoordinateHandle)
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0)
    }

    private fun postToMain(action: () -> Unit) {
        mainHandler.post(action)
    }

    private companion object {
        const val VERTEX_SHADER = """
            attribute vec4 a_Position;
            attribute vec2 a_TexCoord;
            varying vec2 v_TexCoord;
            void main() {
                gl_Position = a_Position;
                v_TexCoord = a_TexCoord;
            }
        """

        const val FRAGMENT_SHADER = """
            #extension GL_OES_EGL_image_external : require
            precision mediump float;
            uniform samplerExternalOES sTexture;
            varying vec2 v_TexCoord;
            void main() {
                gl_FragColor = texture2D(sTexture, v_TexCoord);
            }
        """
    }
}

private fun createExternalTexture(): Int {
    val textures = IntArray(1)
    GLES20.glGenTextures(1, textures, 0)
    GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[0])
    GLES20.glTexParameteri(
        GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
        GLES20.GL_TEXTURE_MIN_FILTER,
        GLES20.GL_LINEAR
    )
    GLES20.glTexParameteri(
        GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
        GLES20.GL_TEXTURE_MAG_FILTER,
        GLES20.GL_LINEAR
    )
    GLES20.glTexParameteri(
        GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
        GLES20.GL_TEXTURE_WRAP_S,
        GLES20.GL_CLAMP_TO_EDGE
    )
    GLES20.glTexParameteri(
        GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
        GLES20.GL_TEXTURE_WRAP_T,
        GLES20.GL_CLAMP_TO_EDGE
    )
    GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0)
    return textures[0]
}

private fun createProgram(vertexShaderCode: String, fragmentShaderCode: String): Int {
    val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
    val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
    return GLES20.glCreateProgram().also { program ->
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)
    }
}

private fun loadShader(type: Int, shaderCode: String): Int {
    return GLES20.glCreateShader(type).also { shader ->
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
    }
}

private fun floatBufferOf(vararg values: Float): FloatBuffer {
    return ByteBuffer
        .allocateDirect(values.size * Float.SIZE_BYTES)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()
        .apply {
            put(values)
            position(0)
        }
}
