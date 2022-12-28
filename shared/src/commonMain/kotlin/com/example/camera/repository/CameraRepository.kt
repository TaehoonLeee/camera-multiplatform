package com.example.camera.repository

import com.example.camera.model.Camera

expect class CameraRepository {
    fun getCamera(): Camera
}