package com.example.camera.resources

actual fun imageResources(path: String): ByteArray {
	return ClassLoader.getSystemResourceAsStream(path).readBytes()
}