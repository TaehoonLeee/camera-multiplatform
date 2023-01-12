package com.example.camera.view

import com.example.camera.gles.*
import com.example.camera.gles.FULL_RECT_COORDS
import com.example.camera.gles.api.glFloatBuffer
import com.example.camera.gles.program.ProgramType
import com.example.camera.gles.program.TextureProgram
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
import platform.Foundation.NSDefaultRunLoopMode
import platform.Foundation.NSRunLoop
import platform.GLKit.GLKView
import platform.GLKit.GLKViewDelegateProtocol
import platform.QuartzCore.CADisplayLink
import platform.gles3.*
import platform.objc.sel_registerName

class FrameworkTextureView : GLKView, AVCaptureVideoDataOutputSampleBufferDelegateProtocol {

	@OverrideInit
	constructor(frame: CValue<CGRect>) : super(frame)

	@OverrideInit
	constructor(coder: NSCoder) : super(coder)

	private val drawable2d = Drawable2d()
	private lateinit var texProgram: TextureProgram

	private val cvTexture: CVOpenGLESTextureRefVar = nativeHeap.alloc()
	private val textureCache: CVOpenGLESTextureCacheRefVar = nativeHeap.alloc()

	init {
		enableSetNeedsDisplay = false
		context = EAGLContext(kEAGLRenderingAPIOpenGLES3)

		EAGLContext.setCurrentContext(context)

		CVOpenGLESTextureCacheCreate(kCFAllocatorDefault, null, context, null, textureCache.ptr)
		createResources()

		val displayLink = CADisplayLink.displayLinkWithTarget(this, sel_registerName("render"))
		displayLink.addToRunLoop(NSRunLoop.currentRunLoop, NSDefaultRunLoopMode)
	}

	override fun captureOutput(output: AVCaptureOutput, didOutputSampleBuffer: CMSampleBufferRef?, fromConnection: AVCaptureConnection) {
		val imageBuffer = CMSampleBufferGetImageBuffer(didOutputSampleBuffer) ?: return
		val (width, height) = CVPixelBufferGetWidth(imageBuffer) to CVPixelBufferGetHeight(imageBuffer)

		CVOpenGLESTextureCacheFlush(textureCache.value, 0)

		CVOpenGLESTextureCacheCreateTextureFromImage(
			planeIndex = 0,
			format = GL_BGRA,
			width = width.toInt(),
			target = GL_TEXTURE_2D,
			type = GL_UNSIGNED_BYTE,
			height = height.toInt(),
			textureAttributes = null,
			internalFormat = GL_RGBA,
			sourceImage = imageBuffer,
			textureOut = cvTexture.ptr,
			allocator = kCFAllocatorDefault,
			textureCache = textureCache.value
		)

		texProgram.draw(
			texStride = drawable2d.texCoordStride,
			vertexStride = drawable2d.vertexStride,
			coordsPerVertex = drawable2d.coordsPerVertex,
			vertexBuffer = FULL_RECT_COORDS.refTo(0),
			texBuffer = FULL_RECT_TEX_COORDS.refTo(0),
			target = CVOpenGLESTextureGetTarget(cvTexture.value).toInt(),
			textureId = CVOpenGLESTextureGetName(cvTexture.value).toInt()
		)

		CFRelease(cvTexture.value)
	}

	private fun createResources() {
		texProgram = TextureProgram(ProgramType.TEXTURE_2D)
	}

	@ObjCAction
	private fun render() {
		display()
	}
}