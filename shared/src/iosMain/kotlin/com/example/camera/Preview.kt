package com.example.camera

import com.example.camera.camera.Device
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

		val device = Device()
		val textureView = FrameworkTextureView(UIScreen.mainScreen.bounds)
		view.addSubview(textureView)
		device.setOutput(textureView)
	}
}