package com.example.camera

import com.example.camera.model.Camera
import com.example.camera.view.FrameworkTextureView
import org.jetbrains.skiko.GenericSkikoView
import org.jetbrains.skiko.SkiaLayer
import org.jetbrains.skiko.SkikoUIView
import org.jetbrains.skiko.SkikoViewController
import platform.AVFoundation.AVCaptureDevice
import platform.Metal.MTLDeviceProtocol

class PreviewControllerBuilder {

	private val camera = Camera()
	private val textureView = FrameworkTextureView()

	fun setMetalDevice(device: MTLDeviceProtocol) {
		textureView.configuration(device)
	}

	fun setVideoDevice(device: AVCaptureDevice) {
		camera.setDevice(device)
	}

	fun create(videoSettings: Map<Any?, *>): SkikoViewController {
		camera.preview()
		camera.setOutput(videoSettings, textureView.bufferProcessor)

		return SkikoViewController(
			SkikoUIView(
				SkiaLayer().apply {
					skikoView = GenericSkikoView(this, textureView)
				}
			)
		)
	}
}