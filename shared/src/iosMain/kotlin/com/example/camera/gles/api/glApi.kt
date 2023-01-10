package com.example.camera.gles.api

import kotlinx.cinterop.cValuesOf
import kotlinx.cinterop.cstr
import kotlinx.cinterop.memScoped

actual val GL_TEXTURE_2D = platform.gles3.GL_TEXTURE_2D
actual val GL_TEXTURE_EXT: Int = throw NoSuchElementException()

actual val GL_VERTEX_SHADER = platform.gles3.GL_VERTEX_SHADER
actual val GL_FRAGMENT_SHADER = platform.gles3.GL_FRAGMENT_SHADER

actual fun glCreateProgram() = platform.gles3.glCreateProgram().toInt()
actual fun glAttachShader(program: Int, shader: Int) = platform.gles3.glAttachShader(program.toUInt(), shader.toUInt())
actual fun glLinkProgram(program: Int) = platform.gles3.glLinkProgram(program.toUInt())

actual fun glCreateShader(type: Int): Int = platform.gles3.glCreateProgram().toInt()
actual fun glShaderSource(shader: Int, source: String) = memScoped {
	platform.gles3.glShaderSource(shader.toUInt(), 1, cValuesOf(source.cstr.ptr), null)
}
actual fun glCompileShader(shader: Int) = platform.gles3.glCompileShader(shader.toUInt())