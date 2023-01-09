package com.example.camera.gles

internal actual val BYPASS_VERTEX_SHADER = """
uniform mat4 uMVPMatrix;
uniform mat4 uTexMatrix;

attribute vec4 aPosition;
attribute vec4 aTextureCoord;

varying vec2 vTextureCoord;

void main() {
    gl_Position = uMVPMatrix * aPosition;
    vTextureCoord = (uTexMatrix * aTextureCoord).xy;
}
"""

internal actual val BYPASS_FRAGMENT_SHADER = """
#extension GL_OES_EGL_image_external : require

precision mediump float;
varying vec2 vTextureCoord;
uniform samplerExternalOES sTexture;

void main() {
	gl_FragColor = texture2D(sTexture, vTextureCoord);
}
"""