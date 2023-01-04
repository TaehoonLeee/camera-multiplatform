package com.example.camera.view

import kotlinx.cinterop.*
import org.jetbrains.skia.*
import org.jetbrains.skiko.SkikoView
import platform.CoreFoundation.kCFAllocatorDefault
import platform.CoreVideo.CVMetalTextureCacheCreate
import platform.CoreVideo.CVMetalTextureCacheRefVar
import platform.Metal.*
import platform.MetalKit.MTKView
import platform.UIKit.UIScreen

class FrameworkTextureView : SkikoView {

	lateinit var bufferProcessor: CaptureBufferProcessor

	private lateinit var commandQueue: MTLCommandQueueProtocol
	private val mtkView = MTKView(frame = UIScreen.mainScreen.bounds)

	private val textureCache: CVMetalTextureCacheRefVar = memScoped {
		cValue<CVMetalTextureCacheRefVar>().getPointer(this).pointed
	}

	fun configuration(device: MTLDeviceProtocol) {
		mtkView.setDevice(device)
		commandQueue = device.newCommandQueue()!!

		CVMetalTextureCacheCreate(kCFAllocatorDefault, null, device, null, textureCache.ptr)

		bufferProcessor = CaptureBufferProcessor(mtkView, commandQueue, textureCache)
	}

	override fun onRender(canvas: Canvas, width: Int, height: Int, nanoTime: Long) {
		if (::commandQueue.isInitialized) {
			val directContext = DirectContext.makeMetal(mtkView.device.objcPtr(), commandQueue.objcPtr())
			val surface = Surface.makeFromMTKView(
				sampleCount = 0,
				context = directContext,
				mtkViewPtr = mtkView.objcPtr(),
				colorSpace = ColorSpace.sRGB,
				surfaceProps = SurfaceProps(),
				origin = SurfaceOrigin.BOTTOM_LEFT,
				colorFormat = SurfaceColorFormat.RGBA_8888
			)
		}
	}
}