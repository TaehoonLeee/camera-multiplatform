package com.example.camera.gles.api

import kotlinx.cinterop.*
import platform.gles3.GL_FALSE
import platform.gles3.GL_TRUE
import platform.glescommon.GLcharVar

@Suppress("ACTUAL_WITHOUT_EXPECT", "ACTUAL_TYPE_ALIAS_WITH_COMPLEX_SUBSTITUTION")
actual typealias glBuffer = CValuesRef<ByteVar>

@Suppress("ACTUAL_WITHOUT_EXPECT", "ACTUAL_TYPE_ALIAS_WITH_COMPLEX_SUBSTITUTION")
actual typealias glFloatBuffer = CValuesRef<FloatVar>

@Suppress("ACTUAL_WITHOUT_EXPECT", "ACTUAL_TYPE_ALIAS_WITH_COMPLEX_SUBSTITUTION")
actual typealias glIntBuffer = CValuesRef<IntVar>

actual val GL_FLOAT = platform.gles3.GL_FLOAT
actual val GL_TRIANGLE_STRIP = platform.gles3.GL_TRIANGLE_STRIP

actual val GL_TEXTURE0 = platform.gles3.GL_TEXTURE0
actual val GL_TEXTURE_2D = platform.gles3.GL_TEXTURE_2D
actual val GL_TEXTURE_EXT = 0

actual val GL_VERTEX_SHADER = platform.gles3.GL_VERTEX_SHADER
actual val GL_FRAGMENT_SHADER = platform.gles3.GL_FRAGMENT_SHADER

actual val GL_LINEAR = platform.gles3.GL_LINEAR
actual val GL_NEAREST = platform.gles3.GL_NEAREST
actual val GL_CLAMP_TO_EDGE = platform.gles3.GL_CLAMP_TO_EDGE
actual val GL_TEXTURE_WRAP_S = platform.gles3.GL_TEXTURE_WRAP_S
actual val GL_TEXTURE_WRAP_T = platform.gles3.GL_TEXTURE_WRAP_T
actual val GL_TEXTURE_MIN_FILTER = platform.gles3.GL_TEXTURE_MIN_FILTER
actual val GL_TEXTURE_MAG_FILTER = platform.gles3.GL_TEXTURE_MAG_FILTER

actual val GL_LINK_STATUS = platform.gles3.GL_COMPILE_STATUS
actual val GL_COMPILE_STATUS = platform.gles3.GL_COMPILE_STATUS

actual fun glCreateProgram() = platform.gles3.glCreateProgram().toInt()
actual fun glAttachShader(program: Int, shader: Int) = platform.gles3.glAttachShader(program.convert(), shader.convert())
actual fun glLinkProgram(program: Int) = platform.gles3.glLinkProgram(program.convert())
actual fun glUseProgram(program: Int) = platform.gles3.glUseProgram(program.convert())

actual fun glCreateShader(type: Int): Int = platform.gles3.glCreateShader(type.convert()).toInt()
actual fun glShaderSource(shader: Int, source: String) = memScoped {
	platform.gles3.glShaderSource(shader.convert(), 1, cValuesOf(source.cstr.ptr), null)
}
actual fun glCompileShader(shader: Int) = platform.gles3.glCompileShader(shader.convert())

actual fun glGetAttribLocation(program: Int, name: String) = platform.gles3.glGetAttribLocation(program.convert(), name)
actual fun glGetUniformLocation(program: Int, name: String) = platform.gles3.glGetUniformLocation(program.convert(), name)

actual fun glActiveTexture(texture: Int) = platform.gles3.glActiveTexture(texture.convert())
actual fun glGenTextures(n: Int, textures: IntArray) = platform.gles3.glGenTextures(n, textures.toUIntArray().refTo(0))
actual fun glBindTexture(target: Int, texture: Int) = platform.gles3.glBindTexture(target.convert(), texture.convert())
actual fun glTexParameteri(target: Int, pname: Int, param: Int) = platform.gles3.glTexParameteri(target.convert(), pname.convert(), param)

actual fun glUniformMatrix4fv(location: Int, count: Int, transpose: Boolean, value: FloatArray, index: Int) {
	val nativeTranspose = if (transpose) GL_TRUE else GL_FALSE
	platform.gles3.glUniformMatrix4fv(location, count, nativeTranspose.convert(), value.refTo(0))
}
actual fun glEnableVertexAttribArray(location: Int) = platform.gles3.glEnableVertexAttribArray(location.convert())
actual fun glVertexAttribPointer(location: Int, size: Int, type: Int, normalized: Boolean, stride: Int, value: glFloatBuffer) {
	val nativeNormalized = if (normalized) GL_TRUE else GL_FALSE
	platform.gles3.glVertexAttribPointer(location.convert(), size, type.convert(), nativeNormalized.convert(), stride, value)
}
actual fun glDrawArrays(mode: Int, first: Int, count: Int) = platform.gles3.glDrawArrays(mode.convert(), first, count)

actual fun glTexImage2D(target: Int, width: Int, height: Int, pixels: glBuffer?) = platform.gles3.glTexImage2D(target.convert(), 0, -1, width, height, 0, 0, 0, pixels)

actual fun glGetProgramiv(program: Int, pname: Int, params: glIntBuffer) = platform.gles3.glGetProgramiv(program.convert(), pname.convert(), params)
actual fun glGetProgramInfoLog(program: Int): String = memScoped {
	val log = allocArray<ByteVar>(512)
	platform.gles3.glGetProgramInfoLog(program.convert(), 512, null, log)

	return log.toKString()
}

actual fun glGetShaderiv(shader: Int, pname: Int, params: glIntBuffer) = platform.gles3.glGetShaderiv(shader.convert(), pname.convert(), params)
actual fun glGetShaderInfoLog(shader: Int): String = memScoped {
	val log = allocArray<ByteVar>(512)
	platform.gles3.glGetShaderInfoLog(shader.convert(), 512, null, log)

	return log.toKString()
}

actual fun glGetError(): Int = platform.gles3.glGetError().toInt()