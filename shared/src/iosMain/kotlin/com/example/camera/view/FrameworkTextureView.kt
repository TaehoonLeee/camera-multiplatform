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
import platform.Foundation.NSError
import platform.Metal.*
import platform.MetalKit.MTKView

@ExportObjCClass
class FrameworkTextureView : MTKView, AVCaptureVideoDataOutputSampleBufferDelegateProtocol {

	@OverrideInit
	constructor(frame: CValue<CGRect>, device: MTLDeviceProtocol?) : super(frame, device) {
		this.device = device
	}

	private val commandQueue = device?.newCommandQueue()
	private val textureCache: CVMetalTextureCacheRefVar = memScoped { alloc() }
	private val pipelineState = run {
		val library = requireNotNull(device?.newDefaultLibrary())
		val function = requireNotNull(library.newFunctionWithName("bypassKernel"))
		val error: CPointer<ObjCObjectVar<NSError?>>? = null
		requireNotNull(device?.newComputePipelineStateWithFunction(function, error))
	}

	init {
		this.framebufferOnly = false
		CVMetalTextureCacheCreate(kCFAllocatorDefault, null, device!!, null, textureCache.ptr)
	}

	override fun captureOutput(output: AVCaptureOutput, didOutputSampleBuffer: CMSampleBufferRef?, fromConnection: AVCaptureConnection) {
		val imageBuffer = CMSampleBufferGetImageBuffer(didOutputSampleBuffer)
		val (width, height) = CVPixelBufferGetWidth(imageBuffer) to CVPixelBufferGetHeight(imageBuffer)

		val cvTexture: CVMetalTextureRefVar = memScoped { alloc() }
		val error = CVMetalTextureCacheCreateTextureFromImage(
			kCFAllocatorDefault, textureCache.value, imageBuffer, null, MTLPixelFormatBGRA8Unorm, width, height, 0, cvTexture.ptr
		)

		println(error)

		val texture = CVMetalTextureGetTexture(cvTexture.value)?: error("Could not get texture from texture ref")
		CVBufferRelease(cvTexture.value)

		val commandBuffer = commandQueue?.commandBuffer()
		val computeCommandEncoder = commandBuffer?.computeCommandEncoder()
		computeCommandEncoder?.let {
			computeCommandEncoder.setComputePipelineState(pipelineState)
			computeCommandEncoder.setTexture(texture, 0)
			computeCommandEncoder.setTexture(currentDrawable?.texture, 1)
			computeCommandEncoder.dispatchThreadgroups(texture.threadGroups(), texture.threadGroupCount())
			computeCommandEncoder.endEncoding()

			commandBuffer.presentDrawable(currentDrawable!!)
			commandBuffer.commit()
		}
	}

	private fun MTLTextureProtocol.threadGroupCount() = MTLSizeMake(8, 8, 1)
	private fun MTLTextureProtocol.threadGroups(): CValue<MTLSize> {
		val (textureWidth, textureHeight) = width to height

		return threadGroupCount().useContents {
			val width = (textureWidth + width - 1u) / width
			val height = (textureHeight + height - 1u) / height
			MTLSizeMake(width, height, 1)
		}
	}
}