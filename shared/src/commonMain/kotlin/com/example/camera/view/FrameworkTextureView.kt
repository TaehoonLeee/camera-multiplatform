package com.example.camera.view

internal val BYPASS_VERTEX_SHADER = """
attribute vec4 vPosition;
uniform mat4 texMatrix;
varying vec2 vTextureCoord;

void main() {
    gl_Position = vPosition;
    vec4 texCoord = vec4((vPosition.xy + vec2(1.0, 1.0)) / 2.0, 0.0, 1.0);
    vTextureCoord = (texMatrix * texCoord).xy;
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

internal val FULL_RECT_COORDS = floatArrayOf(
	-1f, -1f, 1f, -1f,
	-1f, 1f, 1f, 1f
)

//expect class FrameworkTextureView() : SkikoView