package com.example.camera.view

import kotlinx.cinterop.*
import platform.AVFoundation.AVCaptureConnection
import platform.AVFoundation.AVCaptureOutput
import platform.AVFoundation.AVCaptureVideoDataOutputSampleBufferDelegateProtocol
import platform.CoreFoundation.kCFAllocatorDefault
import platform.CoreMedia.CMSampleBufferGetImageBuffer
import platform.CoreMedia.CMSampleBufferRef
import platform.CoreVideo.*
import platform.Metal.*
import platform.MetalKit.MTKView
import platform.darwin.NSObject
import platform.darwin.at_texel_format_bgra8_unorm

class CaptureBufferProcessor(
	private val mtkView: MTKView,
	private val commandQueue: MTLCommandQueueProtocol,
	private val textureCache: CVMetalTextureCacheRefVar
) : AVCaptureVideoDataOutputSampleBufferDelegateProtocol, NSObject() {

	override fun captureOutput(output: AVCaptureOutput, didOutputSampleBuffer: CMSampleBufferRef?, fromConnection: AVCaptureConnection) {
		val imageBuffer = CMSampleBufferGetImageBuffer(didOutputSampleBuffer)
		val (width, height) = CVPixelBufferGetWidth(imageBuffer) to CVPixelBufferGetHeight(imageBuffer)

		val cvTexture: CVMetalTextureRefVar = memScoped {
			cValue<CVMetalTextureRefVar>().getPointer(this).pointed
		}
		CVMetalTextureCacheCreateTextureFromImage(kCFAllocatorDefault, textureCache.value, imageBuffer, null, at_texel_format_bgra8_unorm, width, height, 0, cvTexture.ptr)

		val texture = requireNotNull(CVMetalTextureGetTexture(cvTexture.value))
		commandQueue.commandBuffer()?.let {
			val renderPass = MTLRenderPassDescriptor()
			val drawable = requireNotNull(mtkView.currentDrawable)
			renderPass.colorAttachments.objectAtIndexedSubscript(0).apply {
				this.texture = drawable.texture
				this.clearColor = MTLClearColorMake(.0, .0, .0, .0)
				this.storeAction = MTLStoreActionStore
				this.loadAction = MTLLoadActionClear
			}

			val renderEncoder = it.renderCommandEncoderWithDescriptor(renderPass)
			renderEncoder?.setFrontFacingWinding(MTLWindingClockwise)
			renderEncoder?.setVertexBytes(DEFAULT_IMAGE_VERTICES.pin().addressOf(0), DEFAULT_IMAGE_VERTICES.size.toULong() * 4u, 0)
			renderEncoder?.setVertexBytes(DEFAULT_TEXTURE_COORDINATE.pin().addressOf(0), DEFAULT_TEXTURE_COORDINATE.size.toULong() * 4u, 1)
			renderEncoder?.setFragmentTexture(texture, 0)
			renderEncoder?.drawPrimitives(MTLPrimitiveTypeTriangleStrip, 0, 4)
			renderEncoder?.endEncoding()

			it.presentDrawable(drawable)
			it.commit()
		}
	}
}