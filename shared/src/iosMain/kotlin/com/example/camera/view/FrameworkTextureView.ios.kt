package com.example.camera.view

import kotlinx.cinterop.CValue
import platform.AVFoundation.AVCaptureVideoDataOutputSampleBufferDelegateProtocol
import platform.CoreGraphics.CGRect
import platform.Foundation.NSCoder
import platform.GLKit.GLKView

class FrameworkTextureView : GLKView, AVCaptureVideoDataOutputSampleBufferDelegateProtocol {

	@OverrideInit
	constructor(frame: CValue<CGRect>) : super(frame)

	@OverrideInit
	constructor(coder: NSCoder) : super(coder)

}