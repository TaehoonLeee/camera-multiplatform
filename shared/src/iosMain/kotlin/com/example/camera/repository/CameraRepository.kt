package com.example.camera.repository

import com.example.camera.camera.Camera

actual class CameraRepository {
    actual fun getCamera(): Camera {
        return Camera()
    }
}