package com.example.camera.device

internal expect class DeviceRepository() {
    fun getCamera(): Device
}