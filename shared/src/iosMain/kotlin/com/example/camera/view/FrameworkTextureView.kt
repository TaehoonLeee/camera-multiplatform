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
import platform.Foundation.NSError
import platform.GLKit.GLKTextureTarget2D
import platform.GLKit.GLKView
import platform.Metal.*
import platform.MetalKit.MTKView
import platform.gles2.GL_BGRA
import platform.gles2.GL_RGBA
import platform.gles2.GL_TEXTURE_2D
import platform.gles2.GL_UNSIGNED_BYTE

@ExportObjCClass
class FrameworkTextureView : GLKView, AVCaptureVideoDataOutputSampleBufferDelegateProtocol {

	@OverrideInit
	constructor(frame: CValue<CGRect>) : super(frame)

	private val textureCache: CVOpenGLESTextureCacheRefVar = memScoped { alloc() }

	init {
		context = EAGLContext(kEAGLRenderingAPIOpenGLES2)
		EAGLContext.setCurrentContext(context)
		CVOpenGLESTextureCacheCreate(kCFAllocatorDefault, null, context, null, textureCache.ptr)
	}

	override fun captureOutput(output: AVCaptureOutput, didOutputSampleBuffer: CMSampleBufferRef?, fromConnection: AVCaptureConnection) {
		val imageBuffer = CMSampleBufferGetImageBuffer(didOutputSampleBuffer)
		val (width, height) = CVPixelBufferGetWidth(imageBuffer) to CVPixelBufferGetHeight(imageBuffer)
		val cvTexture: CVOpenGLESTextureRefVar = memScoped { alloc() }
		val error = CVOpenGLESTextureCacheCreateTextureFromImage(
			kCFAllocatorDefault,
			textureCache.value,
			imageBuffer,
			null,
			GLKTextureTarget2D,
			GL_RGBA,
			width.toInt(),
			height.toInt(),
			GL_BGRA,
			GL_UNSIGNED_BYTE,
			0,
			cvTexture.ptr
		)
		println(error)

		val texture = CVOpenGLESTextureGetTarget(cvTexture.value)
		CVBufferRelease(cvTexture.value)
	}
}