package com.example.camera.view

import org.jetbrains.skiko.SkikoView

internal val DEFAULT_IMAGE_VERTICES = floatArrayOf(
	-1f, 1f, 1f, 1f,
	-1f, -1f, 1f, -1f
)

internal val DEFAULT_TEXTURE_COORDINATE = floatArrayOf(
	0f, 0f, 1f, 0f,
	0f, 1f, 1f, 1f
)

//expect class FrameworkTextureView() : SkikoView