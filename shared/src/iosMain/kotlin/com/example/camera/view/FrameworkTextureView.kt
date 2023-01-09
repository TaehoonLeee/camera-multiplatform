package com.example.camera.view

import kotlinx.cinterop.*
import platform.AVFoundation.AVCaptureConnection
import platform.AVFoundation.AVCaptureOutput
import platform.AVFoundation.AVCaptureVideoDataOutputSampleBufferDelegateProtocol
import platform.CoreFoundation.kCFAllocatorDefault
import platform.CoreGraphics.CGRect
import platform.CoreMedia.CMSampleBufferGetImageBuffer
import platform.CoreMedia.CMSampleBufferRef
import platform.CoreVideo.*
import platform.EAGL.EAGLContext
import platform.EAGL.kEAGLRenderingAPIOpenGLES2
import platform.GLKit.GLKTextureTarget2D
import platform.GLKit.GLKView
import platform.gles2.*
import platform.glescommon.GLenum
import platform.glescommon.GLuint

@ExportObjCClass
class FrameworkTextureView : GLKView, AVCaptureVideoDataOutputSampleBufferDelegateProtocol {

	@OverrideInit
	constructor(frame: CValue<CGRect>) : super(frame)

	private val vPositionLoc by lazy(LazyThreadSafetyMode.NONE) {
		glGetAttribLocation(program, "vPosition").toUInt()
	}

	private val texMatrixLoc by lazy(LazyThreadSafetyMode.NONE) {
		glGetUniformLocation(program, "texMatrix")
	}

	private val program = glCreateProgram()
	private val textureCache: CVOpenGLESTextureCacheRefVar = memScoped { alloc() }
	private val texMatrix = floatArrayOf(
		1f, 0f, 0f, 0f,
		0f, 1f, 0f, 0f,
		0f, 0f, 1f, 0f,
		0f, 0f, 0f, 1f
	)

	init {
		context = EAGLContext(kEAGLRenderingAPIOpenGLES2)
		EAGLContext.setCurrentContext(context)
		CVOpenGLESTextureCacheCreate(kCFAllocatorDefault, null, context, null, textureCache.ptr)
		createResources()
	}

	override fun captureOutput(output: AVCaptureOutput, didOutputSampleBuffer: CMSampleBufferRef?, fromConnection: AVCaptureConnection) {
		val imageBuffer = CMSampleBufferGetImageBuffer(didOutputSampleBuffer)
		val (width, height) = CVPixelBufferGetWidth(imageBuffer) to CVPixelBufferGetHeight(imageBuffer)
		val cvTexture: CVOpenGLESTextureRefVar = memScoped { alloc() }
		val error = CVOpenGLESTextureCacheCreateTextureFromImage(
			allocator = kCFAllocatorDefault,
			textureCache = textureCache.value,
			sourceImage = imageBuffer,
			textureAttributes = null,
			target = GLKTextureTarget2D,
			internalFormat = GL_RGBA,
			width = width.toInt(),
			height = height.toInt(),
			format = GL_BGRA,
			type = GL_UNSIGNED_BYTE,
			planeIndex = 0,
			textureOut = cvTexture.ptr
		)

		glUseProgram(program)
		glActiveTexture(GL_TEXTURE0)
		glBindTexture(CVOpenGLESTextureGetTarget(cvTexture.value), CVOpenGLESTextureGetName(cvTexture.value))
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)

		glUniformMatrix4fv(texMatrixLoc, 1, GL_FALSE.convert(), texMatrix.refTo(0))

		glEnableVertexAttribArray(vPositionLoc)
		glVertexAttribPointer(vPositionLoc, 2, GL_FLOAT, GL_FALSE.convert(), 8, FULL_RECT_COORDS.refTo(0))

		glDrawArrays(GL_TRIANGLE_STRIP, 0, 4)
	}

	private fun createResources() = memScoped {
		val vertexShader = createShader(GL_VERTEX_SHADER.convert(), BYPASS_VERTEX_SHADER)
		val fragmentShader = createShader(GL_FRAGMENT_SHADER.convert(), BYPASS_FRAGMENT_SHADER)
		glAttachShader(program, vertexShader)
		glAttachShader(program, fragmentShader)
		glLinkProgram(program)
	}

	private fun MemScope.createShader(type: GLenum, source: String): GLuint {
		val shader = glCreateShader(type)
		glShaderSource(shader, 1, cValuesOf(source.cstr.ptr), null)
		glCompileShader(shader)

		return shader
	}
}