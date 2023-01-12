package com.example.camera.gles

internal expect val FULL_RECT_COORDS: FloatArray
internal val FULL_RECT_TEX_COORDS = floatArrayOf(0f, 0f, 1f, 0f, 0f, 1f, 1f, 1f)

class Drawable2d {

	val coordsPerVertex = 2
	val texCoordStride = 2 * Float.SIZE_BYTES
	val vertexStride = coordsPerVertex * Float.SIZE_BYTES

}