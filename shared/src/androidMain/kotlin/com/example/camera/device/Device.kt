package com.example.camera.device

import android.content.Context
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.view.Surface

actual class Device(context: Context) {

    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

    @Suppress("MissingPermission")
    fun open(target: Surface) {
        cameraManager.openCamera("0", createDeviceStateCallback(target), null)
    }

    private fun createDeviceStateCallback(target: Surface) = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            camera.createCaptureSession(listOf(target), createCaptureSessionStateCallback(target), null)
        }
        override fun onDisconnected(camera: CameraDevice) = Unit
        override fun onError(camera: CameraDevice, error: Int) = Unit
    }

    private fun createCaptureSessionStateCallback(target: Surface) = object : CameraCaptureSession.StateCallback() {
        override fun onConfigured(session: CameraCaptureSession) {
            val request = session.device.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW).let {
                it.addTarget(target)
                it.build()
            }
            session.setRepeatingRequest(request, null, null)
        }

        override fun onConfigureFailed(session: CameraCaptureSession) = Unit
    }
}