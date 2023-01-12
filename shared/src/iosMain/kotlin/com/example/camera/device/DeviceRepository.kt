package com.example.camera.device

internal actual class DeviceRepository {
    actual fun getCamera(): Device {
        return Device()
    }
}