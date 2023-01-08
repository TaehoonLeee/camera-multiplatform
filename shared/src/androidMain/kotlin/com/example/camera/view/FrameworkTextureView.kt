package com.example.camera.view

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.*
import android.util.AttributeSet
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.example.camera.camera.Camera
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.IntBuffer

class FrameworkTextureView(context: Context, attributeSet: AttributeSet) :
    SurfaceView(context, attributeSet), SurfaceHolder.Callback, SurfaceTexture.OnFrameAvailableListener {

    private val texMatrix by lazy(LazyThreadSafetyMode.NONE) {
        GLES20.glGetUniformLocation(id, "texMatrix")
    }

    private val vPosition by lazy(LazyThreadSafetyMode.NONE) {
        GLES20.glGetAttribLocation(id, "vPosition")
    }

    private var texId: Int = -1
    private var program: Int = -1
    private val scratchMatrix = FloatArray(16)

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

        val vertexShader = createShader(GLES20.GL_VERTEX_SHADER, BYPASS_VERTEX_SHADER)
        val fragmentShader = createShader(GLES20.GL_FRAGMENT_SHADER, BYPASS_FRAGMENT_SHADER)
        program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)

        val buf = IntBuffer.allocate(1)
        GLES20.glGenBuffers(1, buf)

        texId = buf[0]
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texId)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)

        cameraTexture = SurfaceTexture(texId)
        cameraTexture.setOnFrameAvailableListener(this)
        cameraTexture.setDefaultBufferSize(1920, 1080)
        cameraSurface = Surface(cameraTexture)

        val camera = Camera(context)
        camera.open(cameraSurface)
    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) = Unit
    override fun surfaceDestroyed(p0: SurfaceHolder) = Unit

    override fun onFrameAvailable(p0: SurfaceTexture?) {
        cameraTexture.updateTexImage()

        GLES20.glUseProgram(program)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texId)

        cameraTexture.getTransformMatrix(scratchMatrix)
        GLES20.glUniformMatrix4fv(texMatrix, 1, false, scratchMatrix, 0)

        val nativeBuffer = ByteBuffer.allocateDirect(32)
        nativeBuffer.order(ByteOrder.nativeOrder())
        val vertexBuffer = nativeBuffer.asFloatBuffer()
        vertexBuffer.put(DEFAULT_IMAGE_VERTICES)
        nativeBuffer.position(0)
        vertexBuffer.position(0)
        GLES20.glEnableVertexAttribArray(vPosition)
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
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

    private fun createShader(type: Int, source: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, source)
        GLES20.glCompileShader(shader)

        return shader
    }
}