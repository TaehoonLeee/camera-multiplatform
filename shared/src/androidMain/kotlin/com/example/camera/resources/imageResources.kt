package com.example.camera.resources

actual fun imageResources(path: String): ByteArray {
	val classLoader = Thread.currentThread().contextClassLoader
	val resource = classLoader?.getResourceAsStream(path)

	return resource?.readBytes()?: ByteArray(1)
}