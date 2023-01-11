package com.example.camera.gles.api

expect abstract class glFloatBuffer

expect val GL_FLOAT: Int
expect val GL_TRIANGLE_STRIP: Int

expect val GL_TEXTURE0: Int
expect val GL_TEXTURE_2D: Int
expect val GL_TEXTURE_EXT: Int

expect val GL_VERTEX_SHADER: Int
expect val GL_FRAGMENT_SHADER: Int

expect val GL_LINEAR: Int
expect val GL_NEAREST: Int
expect val GL_CLAMP_TO_EDGE: Int
expect val GL_TEXTURE_WRAP_S: Int
expect val GL_TEXTURE_WRAP_T: Int
expect val GL_TEXTURE_MAG_FILTER: Int
expect val GL_TEXTURE_MIN_FILTER: Int

expect fun glCreateProgram(): Int
expect fun glAttachShader(program: Int, shader: Int)
expect fun glLinkProgram(program: Int)
expect fun glUseProgram(program: Int)

expect fun glCreateShader(type: Int): Int
expect fun glShaderSource(shader: Int, source: String)
expect fun glCompileShader(shader: Int)

expect fun glGetAttribLocation(program: Int, name: String): Int
expect fun glGetUniformLocation(program: Int, name: String): Int

expect fun glActiveTexture(texture: Int)
expect fun glGenTextures(n: Int, textures: IntArray)
expect fun glBindTexture(target: Int, texture: Int)
expect fun glTexParameteri(target: Int, pname: Int, param: Int)

expect fun glUniformMatrix4fv(location: Int, count: Int, transpose: Boolean, value: FloatArray, index: Int)
expect fun glEnableVertexAttribArray(location: Int)
expect fun glVertexAttribPointer(location: Int, size: Int, type: Int, normalized: Boolean, stride: Int, value: glFloatBuffer)
expect fun glDrawArrays(mode: Int, first: Int, count: Int)