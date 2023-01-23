package com.example.camera.view

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.*
import android.util.AttributeSet
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.example.camera.device.Device
import com.example.camera.gles.Drawable2d
import com.example.camera.gles.FULL_RECT_COORDS
import com.example.camera.gles.FULL_RECT_TEX_COORDS
import com.example.camera.gles.program.ProgramType
import com.example.camera.gles.program.TextureProgram
import com.example.camera.resources.imageResources
import java.nio.ByteBuffer
import java.nio.ByteOrder

class FrameworkTextureView(context: Context, attributeSet: AttributeSet) :
    SurfaceHolder.Callback,
    SurfaceView(context, attributeSet),
    SurfaceTexture.OnFrameAvailableListener {

    private companion object {
        private val FULL_RECT_BUF = run {
            val nativeBuffer = ByteBuffer.allocateDirect(32)
            nativeBuffer.order(ByteOrder.nativeOrder())
            nativeBuffer.asFloatBuffer().also {
                it.put(FULL_RECT_COORDS)
                it.position(0)
            }
        }

        private val FULL_RECT_TEX_BUF = run {
            val nativeBuffer = ByteBuffer.allocateDirect(32)
            nativeBuffer.order(ByteOrder.nativeOrder())
            nativeBuffer.asFloatBuffer().also {
                it.put(FULL_RECT_TEX_COORDS)
                it.position(0)
            }
        }
    }

    private var texId: Int = -1
    private val drawable2d = Drawable2d()
    private val texMatrix = FloatArray(16)

    private lateinit var texProgram: TextureProgram

    private lateinit var eglConfig: EGLConfig
    private lateinit var eglContext: EGLContext
    private lateinit var eglDisplay: EGLDisplay
    private lateinit var eglSurface: EGLSurface

    private lateinit var cameraSurface: Surface
    private lateinit var cameraTexture: SurfaceTexture

    init {
        holder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        initEGL()
        createResources()

        val device = Device(context)
        device.open(cameraSurface)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) = Unit
    override fun surfaceDestroyed(holder: SurfaceHolder) = Unit

    override fun onFrameAvailable(p0: SurfaceTexture?) {
        cameraTexture.updateTexImage()

        cameraTexture.getTransformMatrix(texMatrix)
        texProgram.draw(
            textureId = texId,
            texMatrix = texMatrix,
            coordsPerVertex = drawable2d.coordsPerVertex,
            vertexStride = drawable2d.vertexStride,
            vertexBuffer = FULL_RECT_BUF,
            texStride = drawable2d.texCoordStride,
            texBuffer = FULL_RECT_TEX_BUF,
        )
        EGL14.eglSwapBuffers(eglDisplay, eglSurface)
    }

    private fun initEGL() {
        eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
        val version = intArrayOf(0, 0)
        EGL14.eglInitialize(eglDisplay, version, 0, version, 1)

        val configAttribList = intArrayOf(
            EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT or EGLExt.EGL_OPENGL_ES3_BIT_KHR,
            EGL14.EGL_RED_SIZE, 8,
            EGL14.EGL_GREEN_SIZE, 8,
            EGL14.EGL_BLUE_SIZE, 8,
            EGL14.EGL_ALPHA_SIZE, 8,
            EGL14.EGL_DEPTH_SIZE, 0,
            EGL14.EGL_STENCIL_SIZE, 0,
            EGLExt.EGL_RECORDABLE_ANDROID, 1,
            EGL14.EGL_NONE
        )
        val configs = arrayOfNulls<EGLConfig>(1);
        val numConfigs = intArrayOf(1);
        EGL14.eglChooseConfig(eglDisplay, configAttribList, 0, configs, 0, configs.size, numConfigs, 0)
        eglConfig = configs[0]!!

        val contextAttribList = intArrayOf(EGL14.EGL_CONTEXT_CLIENT_VERSION, 3, EGL14.EGL_NONE)
        eglContext = EGL14.eglCreateContext(eglDisplay, eglConfig, EGL14.EGL_NO_CONTEXT, contextAttribList, 0)

        val clientVersion = intArrayOf(0)
        EGL14.eglQueryContext(eglDisplay, eglContext, EGL14.EGL_CONTEXT_CLIENT_VERSION, clientVersion, 0)

        eglSurface = EGL14.eglCreateWindowSurface(eglDisplay, eglConfig, holder.surface, intArrayOf(EGL14.EGL_NONE), 0)
        EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)
    }

    private fun createResources() {
        texProgram = TextureProgram(ProgramType.TEXTURE_EXT)
        texId = texProgram.createTextures()

        cameraTexture = SurfaceTexture(texId)
        cameraTexture.setOnFrameAvailableListener(this)
        cameraTexture.setDefaultBufferSize(1920, 1080)
        cameraSurface = Surface(cameraTexture)
    }
}