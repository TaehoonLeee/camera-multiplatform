package com.example.camera.view

internal val BYPASS_VERTEX_SHADER = """
attribute vec4 vPosition;
uniform mat4 texMatrix;
varying vec2 vTextureCoord;

void main() {
	gl_Position = vPosition;
	vTextureCoord = (texMatrix * vPosition).xy;
}
"""

internal val BYPASS_FRAGMENT_SHADER = """
#extension GL_OES_EGL_image_external : require

precision mediump float;
varying vec2 vTextureCoord;
uniform samplerExternalOES sTexture;

void main() {
	gl_FragColor = texture2D(sTexture, vTextureCoord);
}
"""

internal val DEFAULT_IMAGE_VERTICES = floatArrayOf(
	-1f, 1f, 1f, 1f,
	-1f, -1f, 1f, -1f
)

internal val DEFAULT_TEXTURE_COORDINATE = floatArrayOf(
	0f, 0f, 1f, 0f,
	0f, 1f, 1f, 1f
)

//expect class FrameworkTextureView() : SkikoView