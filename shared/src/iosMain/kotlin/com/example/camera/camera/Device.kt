package com.example.camera.camera

import platform.AVFoundation.*
import platform.CoreVideo.kCVPixelBufferPixelFormatTypeKey
import platform.CoreVideo.kCVPixelFormatType_32BGRA
import platform.Foundation.CFBridgingRelease
import platform.darwin.dispatch_queue_create

actual class Device {

    private val captureSession = AVCaptureSession()
    private val graphics = AVCaptureVideoDataOutput()
    private val camera = requireNotNull(AVCaptureDevice.defaultDeviceWithMediaType(AVMediaTypeVideo)) {
        error("AVCapture Device returns null")
    }

    fun setOutput(
        delegate: AVCaptureVideoDataOutputSampleBufferDelegateProtocol
    ) {
        captureSession.sessionPreset = AVCaptureSessionPreset1920x1080

        val input = AVCaptureDeviceInput(camera, null)
        if (captureSession.canAddInput(input)) {
            captureSession.addInput(input)
        }

        graphics.videoSettings = mapOf(CFBridgingRelease(kCVPixelBufferPixelFormatTypeKey) as String to kCVPixelFormatType_32BGRA)
        graphics.alwaysDiscardsLateVideoFrames = true
        graphics.setSampleBufferDelegate(delegate, dispatch_queue_create("Device Worker", null))
        if (captureSession.canAddOutput(graphics)) {
            captureSession.addOutput(graphics)
        }

        graphics.connectionWithMediaType(AVMediaTypeVideo)?.videoOrientation = AVCaptureVideoOrientationPortrait

        captureSession.startRunning()
    }

}