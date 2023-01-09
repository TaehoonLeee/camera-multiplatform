package com.example.camera.camera

internal expect class DeviceRepository() {
    fun getCamera(): Device
}