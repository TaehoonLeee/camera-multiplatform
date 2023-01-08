package com.example.camera.camera

import android.content.Context
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.view.Surface

actual class Camera(context: Context) {

    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

    @Suppress("MissingPermission")
    fun open(target: Surface) {
        cameraManager.openCamera("0", object : CameraDevice.StateCallback() {
            override fun onOpened(device: CameraDevice) {
                device.createCaptureSession(listOf(target), object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        val request = session.device.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW).let {
                            it.addTarget(target)
                            it.build()
                        }
                        session.setRepeatingRequest(request, null, null)
                    }
                    override fun onConfigureFailed(p0: CameraCaptureSession) = Unit
                }, null)
            }
            override fun onDisconnected(p0: CameraDevice) = Unit
            override fun onError(p0: CameraDevice, p1: Int) = Unit
        }, null)
    }
}