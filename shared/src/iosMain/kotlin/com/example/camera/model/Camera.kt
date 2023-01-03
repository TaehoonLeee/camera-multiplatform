package com.example.camera.model

import platform.AVFoundation.*
import platform.CoreVideo.kCVPixelBufferPixelFormatTypeKey
import platform.CoreVideo.kCVPixelFormatType_32BGRA
import platform.Foundation.NSNumber

actual class Camera {

    private val captureSession = AVCaptureSession()
    private val graphics = AVCaptureVideoDataOutput()

    init {
        captureSession.sessionPreset = AVCaptureSessionPresetInputPriority
    }

    fun preview() {
        if (!captureSession.running) captureSession.startRunning()
    }

    fun setOutput(
        device: AVCaptureDevice,
        delegate: AVCaptureVideoDataOutputSampleBufferDelegateProtocol
    ) {
        captureSession.beginConfiguration()
        captureSession.sessionPreset = AVCaptureSessionPresetInputPriority

        val input = AVCaptureDeviceInput(device, null)
        if (captureSession.canAddInput(input)) {
            captureSession.addInput(input)
        }

        graphics.videoSettings = mapOf(
            kCVPixelBufferPixelFormatTypeKey to NSNumber(kCVPixelFormatType_32BGRA)
        )
        graphics.alwaysDiscardsLateVideoFrames = true
        graphics.setSampleBufferDelegate(delegate, null)
        if (captureSession.canAddOutput(graphics)) {
            captureSession.addOutput(graphics)
        }

        graphics.connectionWithMediaType(AVMediaTypeVideo)?.videoOrientation = AVCaptureVideoOrientationPortrait
        captureSession.commitConfiguration()
    }

}