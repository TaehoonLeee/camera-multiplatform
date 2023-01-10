package com.example.camera.gles.api

expect val GL_TEXTURE_2D: Int
expect val GL_TEXTURE_EXT: Int

expect val GL_VERTEX_SHADER: Int
expect val GL_FRAGMENT_SHADER: Int

expect fun glCreateProgram(): Int
expect fun glAttachShader(program: Int, shader: Int)
expect fun glLinkProgram(program: Int)

expect fun glCreateShader(type: Int): Int
expect fun glShaderSource(shader: Int, source: String)
expect fun glCompileShader(shader: Int)