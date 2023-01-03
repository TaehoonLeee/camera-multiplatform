package com.example.camera.view

import kotlinx.cinterop.CValuesRef
import kotlinx.cinterop.objcPtr
import org.jetbrains.skia.*
import org.jetbrains.skiko.SkikoView
import platform.CoreFoundation.kCFAllocatorDefault
import platform.CoreVideo.CVMetalTextureCacheCreate
import platform.CoreVideo.CVMetalTextureCacheRefVar
import platform.Metal.MTLCommandQueueProtocol
import platform.Metal.MTLComputePipelineStateProtocol
import platform.Metal.MTLCreateSystemDefaultDevice
import platform.MetalKit.MTKView

actual class Preview : MTKView(), SkikoView {

	private val commandQueue: MTLCommandQueueProtocol?
	private val pipelineState: MTLComputePipelineStateProtocol?
	private var textureCache: CValuesRef<CVMetalTextureCacheRefVar>? = null
	init {
		val device = requireNotNull(MTLCreateSystemDefaultDevice()) {
			error("Unable to initialize GPU device")
		}.also(::setDevice)
		commandQueue = device.newCommandQueue()

		val library = device.newDefaultLibrary()
		val function = requireNotNull(library?.newFunctionWithName("passthroughKernel"))
		pipelineState = device.newComputePipelineStateWithFunction(function, null)

		CVMetalTextureCacheCreate(kCFAllocatorDefault, null, device, null, textureCache)
	}

	override fun onRender(canvas: Canvas, width: Int, height: Int, nanoTime: Long) {
		val directContext = DirectContext.makeMetal(device.objcPtr(), commandQueue.objcPtr())
		val surface = Surface.makeFromMTKView(
			sampleCount = 0,
			context = directContext,
			mtkViewPtr = this.objcPtr(),
			colorSpace = ColorSpace.sRGB,
			surfaceProps = SurfaceProps(),
			origin = SurfaceOrigin.BOTTOM_LEFT,
			colorFormat = SurfaceColorFormat.RGBA_8888
		)
		TODO("Not yet implemented")
	}
}