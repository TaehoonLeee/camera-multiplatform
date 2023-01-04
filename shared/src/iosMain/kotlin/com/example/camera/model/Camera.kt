package com.example.camera.model

import kotlinx.cinterop.pointed
import platform.AVFoundation.*
import platform.CoreFoundation.CFStringRefVar
import platform.CoreVideo.kCVPixelBufferPixelFormatTypeKey
import platform.CoreVideo.kCVPixelFormatType_32BGRA
import platform.Foundation.NSNumber
import platform.Foundation.NSString
import platform.darwin.DISPATCH_QUEUE_CONCURRENT

actual class Camera {

    private lateinit var camera: AVCaptureDevice

    private val captureSession = AVCaptureSession()
    private val graphics = AVCaptureVideoDataOutput()

    init {
        captureSession.sessionPreset = AVCaptureSessionPresetInputPriority
    }

    fun setDevice(device: AVCaptureDevice) {
        camera = device
    }

    fun preview() {
        if (!captureSession.running) captureSession.startRunning()
    }

    fun setOutput(
        videoSettings: Map<Any?, *>,
        delegate: AVCaptureVideoDataOutputSampleBufferDelegateProtocol
    ) {
        captureSession.beginConfiguration()
        captureSession.sessionPreset = AVCaptureSessionPresetInputPriority

        val input = AVCaptureDeviceInput(camera, null)
        if (captureSession.canAddInput(input)) {
            captureSession.addInput(input)
        }

        graphics.videoSettings = videoSettings
        graphics.alwaysDiscardsLateVideoFrames = true
        graphics.setSampleBufferDelegate(delegate, DISPATCH_QUEUE_CONCURRENT)
        if (captureSession.canAddOutput(graphics)) {
            captureSession.addOutput(graphics)
        }

        graphics.connectionWithMediaType(AVMediaTypeVideo)?.videoOrientation = AVCaptureVideoOrientationPortrait
        captureSession.commitConfiguration()
    }

}