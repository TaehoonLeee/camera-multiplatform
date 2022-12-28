package com.example.camera.model

import platform.AVFoundation.*
import platform.CoreMedia.CMSampleBufferRef
import platform.CoreVideo.kCVPixelBufferPixelFormatTypeKey
import platform.CoreVideo.kCVPixelFormatType_32BGRA
import platform.Foundation.NSNumber
import platform.darwin.NSObject
import platform.darwin.NSObjectProtocol

actual class Camera : AVCaptureVideoDataOutputSampleBufferDelegateProtocol, NSObjectProtocol by NSObject() {

    private val captureSession = AVCaptureSession()
    private val graphics = AVCaptureVideoDataOutput()

    init {
        captureSession.beginConfiguration()
        captureSession.sessionPreset = AVCaptureSessionPresetInputPriority

        val device = AVCaptureDevice.defaultDeviceWithMediaType(AVMediaTypeVideo)!!
        val input = AVCaptureDeviceInput(device, null)

        if (captureSession.canAddInput(input)) {
            captureSession.addInput(input)
        }

        val preview = AVCaptureVideoPreviewLayer(captureSession)
        preview.videoGravity = AVLayerVideoGravityResizeAspectFill
        preview.connection?.videoOrientation = AVCaptureVideoOrientationPortrait

        graphics.videoSettings = mapOf(
            kCVPixelBufferPixelFormatTypeKey to NSNumber(kCVPixelFormatType_32BGRA)
        )
        graphics.alwaysDiscardsLateVideoFrames = true
        graphics.setSampleBufferDelegate(this, null)
        if (captureSession.canAddOutput(graphics)) {
            captureSession.addOutput(graphics)
        }

        graphics.connectionWithMediaType(AVMediaTypeVideo)?.videoOrientation = AVCaptureVideoOrientationPortrait
        captureSession.commitConfiguration()
    }

    override fun captureOutput(
        output: AVCaptureOutput,
        didOutputSampleBuffer: CMSampleBufferRef?,
        fromConnection: AVCaptureConnection
    ) {
        super.captureOutput(output, didOutputSampleBuffer, fromConnection)
    }

}