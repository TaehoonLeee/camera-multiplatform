package com.example.camera.resources

import kotlinx.cinterop.refTo
import platform.Foundation.NSBundle
import platform.Foundation.NSFileManager
import platform.posix.memcpy

actual fun imageResources(path: String): ByteArray {
	val resourcePath = "${NSBundle.mainBundle.resourcePath}/$path"
	val contents = NSFileManager.defaultManager.contentsAtPath(resourcePath)

	if (contents != null) {
		val pixels = ByteArray(contents.length.toInt())
		memcpy(pixels.refTo(0), contents.bytes, contents.length)

		return pixels
	} else {
		throw NullPointerException("$path, $resourcePath")
	}
}