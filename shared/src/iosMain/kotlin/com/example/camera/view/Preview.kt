package com.example.camera.view

import kotlinx.cinterop.CValuesRef
import kotlinx.cinterop.objcPtr
import org.jetbrains.skia.*
import org.jetbrains.skiko.SkikoView
import platform.CoreFoundation.kCFAllocatorDefault
import platform.CoreVideo.CVMetalTextureCacheCreate
import platform.CoreVideo.CVMetalTextureCacheRefVar
import platform.Metal.*
import platform.MetalKit.MTKView
import platform.darwin.at_texel_format_bgra8_unorm

actual class Preview : MTKView(), SkikoView {

	private val commandQueue: MTLCommandQueueProtocol?
	private val pipelineState: MTLRenderPipelineStateProtocol?
	private var textureCache: CValuesRef<CVMetalTextureCacheRefVar>? = null

	init {
		val device = requireNotNull(MTLCreateSystemDefaultDevice()) {
			error("Unable to initialize GPU device")
		}.also(::setDevice)
		commandQueue = device.newCommandQueue()

		val library = device.newDefaultLibrary()
		val vertexFunction = requireNotNull(library?.newFunctionWithName("vertexPassThrough"))
		val fragmentFunction = requireNotNull(library?.newFunctionWithName("fragmentPassThrough"))

		val pipelineDescriptor = MTLRenderPipelineDescriptor()
		pipelineDescriptor.vertexFunction = vertexFunction
		pipelineDescriptor.fragmentFunction = fragmentFunction
		pipelineDescriptor.colorAttachments.objectAtIndexedSubscript(0).pixelFormat = at_texel_format_bgra8_unorm

		pipelineState = device.newRenderPipelineStateWithDescriptor(
			pipelineDescriptor, MTLPipelineOptionNone,null, null
		)

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