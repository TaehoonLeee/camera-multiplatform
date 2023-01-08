package com.example.camera.repository

import com.example.camera.camera.Camera

expect class CameraRepository {
    fun getCamera(): Camera
}