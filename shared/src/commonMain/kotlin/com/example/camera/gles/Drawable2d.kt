package com.example.camera.gles

internal expect val FULL_RECT_COORDS: FloatArray
internal val FULL_RECT_TEX_COORDS = floatArrayOf(0f, 0f, 1f, 0f, 0f, 1f, 1f, 1f)

class Drawable2d {

	val coordsPerVertex = 2
	val vertexStride = coordsPerVertex * Float.SIZE_BYTES
	val vertexCount = FULL_RECT_COORDS.size / coordsPerVertex

	val vertexArray = FULL_RECT_COORDS
	val texCoordArray = FULL_RECT_TEX_COORDS

}