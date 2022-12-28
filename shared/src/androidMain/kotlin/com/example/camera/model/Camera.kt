package com.example.camera.model

import android.content.Context
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest

actual class Camera(context: Context) {

    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

    private lateinit var device: CameraDevice
    private lateinit var session: CameraCaptureSession
    private lateinit var previewRequestBuilder: CaptureRequest.Builder

    private val characteristic get() = cameraManager.getCameraCharacteristics(device.id)

}