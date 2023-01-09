package com.example.camera.camera

internal actual class DeviceRepository {
    actual fun getCamera(): Device {
        return Device()
    }
}