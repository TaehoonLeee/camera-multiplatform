package com.example.camera

import com.example.camera.camera.Camera
import com.example.camera.view.FrameworkTextureView
import platform.Foundation.NSCoder
import platform.Metal.MTLCreateSystemDefaultDevice
import platform.UIKit.UIScreen
import platform.UIKit.UIViewController
import platform.UIKit.addSubview

class PreviewControllerBuilder {
	fun create() = PreviewController()
}

class PreviewController : UIViewController {

	@OverrideInit
	constructor() : super(nibName = null, bundle = null)

	@OverrideInit
	constructor(coder: NSCoder) : super(coder)

	override fun viewDidLoad() {
		super.viewDidLoad()

		val camera = Camera()
		val textureView = FrameworkTextureView(UIScreen.mainScreen.bounds, MTLCreateSystemDefaultDevice())
		view.addSubview(textureView)
		camera.setOutput(textureView)
	}
}