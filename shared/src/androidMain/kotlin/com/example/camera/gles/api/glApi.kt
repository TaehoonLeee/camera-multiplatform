package com.example.camera.gles.api

import android.opengl.GLES11Ext
import android.opengl.GLES20
import java.nio.Buffer
import java.nio.FloatBuffer
import java.nio.IntBuffer

actual typealias glBuffer = Buffer
actual typealias glIntBuffer = IntBuffer
actual typealias glFloatBuffer = FloatBuffer

actual val GL_FLOAT = GLES20.GL_FLOAT
actual val GL_TRIANGLE_STRIP = GLES20.GL_TRIANGLE_STRIP

actual val GL_TEXTURE0 = GLES20.GL_TEXTURE0
actual val GL_TEXTURE_2D = GLES20.GL_TEXTURE_2D
actual val GL_TEXTURE_EXT = GLES11Ext.GL_TEXTURE_EXTERNAL_OES

actual val GL_VERTEX_SHADER = GLES20.GL_VERTEX_SHADER
actual val GL_FRAGMENT_SHADER = GLES20.GL_FRAGMENT_SHADER

actual val GL_LINEAR = GLES20.GL_LINEAR
actual val GL_NEAREST = GLES20.GL_NEAREST
actual val GL_CLAMP_TO_EDGE = GLES20.GL_CLAMP_TO_EDGE
actual val GL_TEXTURE_WRAP_S = GLES20.GL_TEXTURE_WRAP_S
actual val GL_TEXTURE_WRAP_T = GLES20.GL_TEXTURE_WRAP_T
actual val GL_TEXTURE_MIN_FILTER = GLES20.GL_TEXTURE_MIN_FILTER
actual val GL_TEXTURE_MAG_FILTER = GLES20.GL_TEXTURE_MAG_FILTER

actual val GL_LINK_STATUS = GLES20.GL_COMPILE_STATUS
actual val GL_COMPILE_STATUS = GLES20.GL_COMPILE_STATUS

actual fun glCreateProgram() = GLES20.glCreateProgram()
actual fun glAttachShader(program: Int, shader: Int) = GLES20.glAttachShader(program, shader)
actual fun glLinkProgram(program: Int) = GLES20.glLinkProgram(program)
actual fun glUseProgram(program: Int) = GLES20.glUseProgram(program)

actual fun glCreateShader(type: Int) = GLES20.glCreateShader(type)
actual fun glShaderSource(shader: Int, source: String) = GLES20.glShaderSource(shader, source)
actual fun glCompileShader(shader: Int) = GLES20.glCompileShader(shader)

actual fun glGetAttribLocation(program: Int, name: String) = GLES20.glGetAttribLocation(program, name)
actual fun glGetUniformLocation(program: Int, name: String) = GLES20.glGetUniformLocation(program, name)

actual fun glActiveTexture(texture: Int) = GLES20.glActiveTexture(texture)
actual fun glGenTextures(n: Int, textures: IntArray) = GLES20.glGenTextures(n ,textures, 0)
actual fun glBindTexture(target: Int, texture: Int) = GLES20.glBindTexture(target, texture)
actual fun glTexParameteri(target: Int, pname: Int, param: Int) = GLES20.glTexParameteri(target, pname, param)

actual fun glUniformMatrix4fv(location: Int, count: Int, transpose: Boolean, value: FloatArray, index: Int) = GLES20.glUniformMatrix4fv(location, count, transpose, value, index)
actual fun glEnableVertexAttribArray(location: Int) = GLES20.glEnableVertexAttribArray(location)
actual fun glVertexAttribPointer(location: Int, size: Int, type: Int, normalized: Boolean, stride: Int, value: glFloatBuffer) = GLES20.glVertexAttribPointer(location, size, type, normalized, stride, value)
actual fun glDrawArrays(mode: Int, first: Int, count: Int) = GLES20.glDrawArrays(mode, first, count)

actual fun glTexImage2D(target: Int, width: Int, height: Int, pixels: glBuffer?) = GLES20.glTexImage2D(target, 0, -1, width, height, 0, 0, 0, pixels)

actual fun glGetProgramiv(program: Int, pname: Int, params: glIntBuffer) = GLES20.glGetProgramiv(program, pname, params)
actual fun glGetProgramInfoLog(program: Int) = GLES20.glGetProgramInfoLog(program)
actual fun glGetShaderiv(shader: Int, pname: Int, params: glIntBuffer) = GLES20.glGetShaderiv(shader, pname, params)
actual fun glGetShaderInfoLog(shader: Int) = GLES20.glGetShaderInfoLog(shader)

actual fun glGetError() = GLES20.glGetError()