package com.example.camera.gles.api

import kotlinx.cinterop.*
import platform.gles3.GL_FALSE
import platform.gles3.GL_TRUE

actual abstract class glFloatBuffer : CValuesRef<FloatVar>()

actual val GL_FLOAT = platform.gles3.GL_FLOAT
actual val GL_TRIANGLE_STRIP = platform.gles3.GL_TRIANGLE_STRIP

actual val GL_TEXTURE0 = platform.gles3.GL_TEXTURE0
actual val GL_TEXTURE_2D = platform.gles3.GL_TEXTURE_2D
actual val GL_TEXTURE_EXT: Int = throw NoSuchElementException()

actual val GL_VERTEX_SHADER = platform.gles3.GL_VERTEX_SHADER
actual val GL_FRAGMENT_SHADER = platform.gles3.GL_FRAGMENT_SHADER

actual val GL_LINEAR = platform.gles3.GL_LINEAR
actual val GL_NEAREST = platform.gles3.GL_NEAREST
actual val GL_CLAMP_TO_EDGE = platform.gles3.GL_CLAMP_TO_EDGE
actual val GL_TEXTURE_WRAP_S = platform.gles3.GL_TEXTURE_WRAP_S
actual val GL_TEXTURE_WRAP_T = platform.gles3.GL_TEXTURE_WRAP_T
actual val GL_TEXTURE_MIN_FILTER = platform.gles3.GL_TEXTURE_MIN_FILTER
actual val GL_TEXTURE_MAG_FILTER = platform.gles3.GL_TEXTURE_MAG_FILTER

actual fun glCreateProgram() = platform.gles3.glCreateProgram().toInt()
actual fun glAttachShader(program: Int, shader: Int) = platform.gles3.glAttachShader(program.toUInt(), shader.toUInt())
actual fun glLinkProgram(program: Int) = platform.gles3.glLinkProgram(program.toUInt())
actual fun glUseProgram(program: Int) = platform.gles3.glUseProgram(program.toUInt())

actual fun glCreateShader(type: Int): Int = platform.gles3.glCreateProgram().toInt()
actual fun glShaderSource(shader: Int, source: String) = memScoped {
	platform.gles3.glShaderSource(shader.toUInt(), 1, cValuesOf(source.cstr.ptr), null)
}
actual fun glCompileShader(shader: Int) = platform.gles3.glCompileShader(shader.toUInt())

actual fun glGetAttribLocation(program: Int, name: String) = platform.gles3.glGetAttribLocation(program.toUInt(), name)

actual fun glActiveTexture(texture: Int) = platform.gles3.glActiveTexture(texture.toUInt())
actual fun glGenTextures(n: Int, textures: IntArray) = platform.gles3.glGenTextures(n, textures.toUIntArray().refTo(0))
actual fun glBindTexture(target: Int, texture: Int) = platform.gles3.glBindTexture(target.convert(), texture.toUInt())
actual fun glTexParameteri(target: Int, pname: Int, param: Int) = platform.gles3.glTexParameteri(target.convert(), pname.toUInt(), param)

actual fun glUniformMatrix4fv(location: Int, count: Int, transpose: Boolean, value: FloatArray, index: Int) {
	val nativeTranspose = if (transpose) GL_TRUE else GL_FALSE
	platform.gles3.glUniformMatrix4fv(location, count, nativeTranspose.convert(), value.refTo(0))
}
actual fun glEnableVertexAttribArray(location: Int) = platform.gles3.glEnableVertexAttribArray(location.toUInt())
actual fun glVertexAttribPointer(location: Int, size: Int, type: Int, normalized: Boolean, stride: Int, value: glFloatBuffer) {
	val nativeNormalized = if (normalized) GL_TRUE else GL_FALSE
	platform.gles3.glVertexAttribPointer(location.toUInt(), size, type.toUInt(), nativeNormalized.convert(), stride, value)
}
actual fun glDrawArrays(mode: Int, first: Int, count: Int) = platform.gles3.glDrawArrays(mode.toUInt(), first, count)