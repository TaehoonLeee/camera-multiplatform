package com.example.camera

import com.example.camera.camera.Device
import com.example.camera.gles.BYPASS_FRAGMENT_SHADER
import com.example.camera.gles.BYPASS_VERTEX_SHADER
import com.example.camera.view.FULL_RECT_COORDS
import com.example.camera.view.FULL_RECT_TEX_COORDS
import kotlinx.cinterop.*
import platform.AVFoundation.AVCaptureConnection
import platform.AVFoundation.AVCaptureOutput
import platform.AVFoundation.AVCaptureVideoDataOutputSampleBufferDelegateProtocol
import platform.CoreFoundation.CFRelease
import platform.CoreFoundation.kCFAllocatorDefault
import platform.CoreGraphics.CGRect
import platform.CoreMedia.CMSampleBufferGetImageBuffer
import platform.CoreMedia.CMSampleBufferRef
import platform.CoreVideo.*
import platform.EAGL.EAGLContext
import platform.EAGL.kEAGLRenderingAPIOpenGLES3
import platform.Foundation.NSCoder
import platform.GLKit.GLKView
import platform.GLKit.GLKViewController
import platform.gles3.*
import platform.glescommon.*

fun createPreviewController() = PreviewController()

class PreviewController : GLKViewController, AVCaptureVideoDataOutputSampleBufferDelegateProtocol {

	@OverrideInit
	constructor() : super(nibName = null, bundle = null)

	@OverrideInit
	constructor(coder: NSCoder) : super(coder)

	private var program = 0u
	private val textureCache: CVOpenGLESTextureCacheRefVar = nativeHeap.alloc()

	private val aPositionLoc by lazy(LazyThreadSafetyMode.NONE) {
		glGetAttribLocation(program, "aPosition").toUInt()
	}
	private val aTextureCoordLoc by lazy(LazyThreadSafetyMode.NONE) {
		glGetAttribLocation(program, "aTextureCoord").toUInt()
	}

	override fun viewDidLoad() {
		super.viewDidLoad()

		val context = EAGLContext(kEAGLRenderingAPIOpenGLES3)
		(view as GLKView).context = context
		EAGLContext.setCurrentContext(context)

		CVOpenGLESTextureCacheCreate(kCFAllocatorDefault, null, context, null, textureCache.ptr)
		createResources()

		Device().setOutput(this)
	}

	override fun glkView(view: GLKView, drawInRect: CValue<CGRect>) {
		glDrawArrays(GL_TRIANGLE_STRIP.convert(), 0, 4)
	}

	override fun captureOutput(output: AVCaptureOutput, didOutputSampleBuffer: CMSampleBufferRef?, fromConnection: AVCaptureConnection) {
		val imageBuffer = CMSampleBufferGetImageBuffer(didOutputSampleBuffer)?: return
		val (width, height) = CVPixelBufferGetWidth(imageBuffer) to CVPixelBufferGetHeight(imageBuffer)

		CVOpenGLESTextureCacheFlush(textureCache.value, 0)

		memScoped {
			val cvTexture: CVOpenGLESTextureRefVar = alloc()
			CVOpenGLESTextureCacheCreateTextureFromImage(
				allocator = kCFAllocatorDefault,
				textureCache = textureCache.value,
				sourceImage = imageBuffer,
				textureAttributes = null,
				target = GL_TEXTURE_2D,
				internalFormat = GL_RGBA,
				width = width.toInt(),
				height = height.toInt(),
				format = GL_BGRA,
				type = GL_UNSIGNED_BYTE,
				planeIndex = 0,
				textureOut = cvTexture.ptr
			)

			glActiveTexture(GL_TEXTURE0)
			glBindTexture(CVOpenGLESTextureGetTarget(cvTexture.value), CVOpenGLESTextureGetName(cvTexture.value))

			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)

			glEnableVertexAttribArray(aPositionLoc)
			glVertexAttribPointer(aPositionLoc, 2, GL_FLOAT, GL_FALSE.convert(), 8, FULL_RECT_COORDS.refTo(0))

			glEnableVertexAttribArray(aTextureCoordLoc)
			glVertexAttribPointer(aTextureCoordLoc, 2, GL_FLOAT, GL_FALSE.convert(), 8, FULL_RECT_TEX_COORDS.refTo(0))

			CFRelease(cvTexture.value)
		}
	}

	private fun createResources() = memScoped {
		val vertexShader = createShader(GL_VERTEX_SHADER.convert(), BYPASS_VERTEX_SHADER)
		val fragmentShader = createShader(GL_FRAGMENT_SHADER.convert(), BYPASS_FRAGMENT_SHADER)

		program = glCreateProgram()
		glAttachShader(program, vertexShader)
		glAttachShader(program, fragmentShader)
		glLinkProgram(program)

		glUseProgram(program)
	}

	private fun MemScope.createShader(type: GLenum, source: String): GLuint {
		val shader = glCreateShader(type)
		glShaderSource(shader, 1, cValuesOf(source.cstr.ptr), null)
		glCompileShader(shader)

		return shader
	}
}