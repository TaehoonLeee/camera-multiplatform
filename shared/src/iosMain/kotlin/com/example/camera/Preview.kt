package com.example.camera

import com.example.camera.device.Device
import com.example.camera.resources.imageResources
import com.example.camera.view.FrameworkTextureView
import platform.Foundation.NSCoder
import platform.UIKit.UIScreen
import platform.UIKit.UIViewController
import platform.UIKit.addSubview

fun createPreviewController() = PreviewController()

class PreviewController : UIViewController {

	@OverrideInit
	constructor() : super(nibName = null, bundle = null)

	@OverrideInit
	constructor(coder: NSCoder) : super(coder)

	override fun viewDidLoad() {
		super.viewDidLoad()

		val textureView = FrameworkTextureView(UIScreen.mainScreen.bounds)
		view.addSubview(textureView)
		imageResources("sample_clut.png").also {
			println(it.toList())
		}
		Device().setOutput(textureView)
	}
}