package com.example.camera.gles.api

import android.opengl.GLES11Ext
import android.opengl.GLES20

actual val GL_TEXTURE_2D = GLES20.GL_TEXTURE_2D
actual val GL_TEXTURE_EXT = GLES11Ext.GL_TEXTURE_EXTERNAL_OES

actual val GL_VERTEX_SHADER = GLES20.GL_VERTEX_SHADER
actual val GL_FRAGMENT_SHADER = GLES20.GL_FRAGMENT_SHADER

actual fun glCreateProgram() = GLES20.glCreateProgram()
actual fun glAttachShader(program: Int, shader: Int) = GLES20.glAttachShader(program, shader)
actual fun glLinkProgram(program: Int) = GLES20.glLinkProgram(program)

actual fun glCreateShader(type: Int) = GLES20.glCreateShader(type)
actual fun glShaderSource(shader: Int, source: String) = GLES20.glShaderSource(shader, source)
actual fun glCompileShader(shader: Int) = GLES20.glCompileShader(shader)