package com.example.camera.gles

internal actual val BYPASS_VERTEX_SHADER = """
attribute vec4 aPosition;
attribute vec4 aTextureCoord;

varying vec2 vTextureCoord;

void main() {
    gl_Position = aPosition;
    vTextureCoord = aTextureCoord.xy;
}
"""

internal actual val BYPASS_FRAGMENT_SHADER = """
precision mediump float;
varying vec2 vTextureCoord;
uniform sampler2D sTexture;

void main() {
	gl_FragColor = texture2D(sTexture, vTextureCoord);
}
"""