package com.example.camera.gles.program

import com.example.camera.gles.api.*

enum class ProgramType {
	TEXTURE_2D, TEXTURE_EXT
}

class TextureProgram(type: ProgramType) {

	private companion object {
		private val VERTEX_SHADER = """
			uniform mat4 uTexMatrix;

			attribute vec4 aPosition;
			attribute vec4 aTextureCoord;
			
			varying vec2 vTextureCoord;
			
			void main() {
				gl_Position = aPosition;
				vTextureCoord = (uTexMatrix * aTextureCoord).xy;
			}
		""".trimIndent()

		private val FRAGMENT_SHADER = """
			precision mediump float;
			varying vec2 vTextureCoord;
			uniform sampler2D sTexture;

			void main() {
				gl_FragColor = texture2D(sTexture, vTextureCoord);
			}
		""".trimIndent()

		private val FRAGMENT_SHADER_EXT = """
			#extension GL_OES_EGL_image_external : require

			precision mediump float;
			varying vec2 vTextureCoord;
			uniform samplerExternalOES sTexture;

			void main() {
				gl_FragColor = texture2D(sTexture, vTextureCoord);
			}
		""".trimIndent()
	}

	private val target: Int
	private val handle: Int

	init {
		when (type) {
			ProgramType.TEXTURE_2D -> {
				target = GL_TEXTURE_2D
				handle = createProgram(VERTEX_SHADER, FRAGMENT_SHADER)
			}
			ProgramType.TEXTURE_EXT -> {
				target = GL_TEXTURE_EXT
				handle = createProgram(VERTEX_SHADER, FRAGMENT_SHADER_EXT)
			}
		}
	}

	private fun createProgram(vertexShaderSource: String, fragmentShaderSource: String): Int {
		val program = glCreateProgram()
		val vertexShader = createShader(GL_VERTEX_SHADER, vertexShaderSource)
		val fragmentShader = createShader(GL_FRAGMENT_SHADER, fragmentShaderSource)

		glAttachShader(program, vertexShader)
		glAttachShader(program, fragmentShader)
		glLinkProgram(program)

		return program
	}

	private fun createShader(type: Int, source: String): Int {
		val shader = glCreateShader(type)
		glShaderSource(shader, source)
		glCompileShader(shader)

		return shader
	}
}