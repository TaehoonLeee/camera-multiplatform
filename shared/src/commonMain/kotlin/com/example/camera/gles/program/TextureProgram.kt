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

	private val aPositionLoc: Int
	private val aTextureCoordLoc: Int
	private val uTexMatrixLoc: Int

	private val identityMatrix = floatArrayOf(
		1f, 0f, 0f, 0f,
		0f, 1f, 0f, 0f,
		0f, 0f, 1f, 0f,
		0f, 0f, 0f, 1f
	)

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

		aPositionLoc = glGetAttribLocation(handle, "aPosition")
		aTextureCoordLoc = glGetAttribLocation(handle, "aTextureCoord")
		uTexMatrixLoc = glGetUniformLocation(handle, "uTexMatrix")
	}

	fun createTextures(): Int {
		val textures = intArrayOf(0)
		glGenTextures(1, textures)

		val texId = textures[0]
		glBindTexture(target, texId)
		glTexParameteri(target, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
		glTexParameteri(target, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
		glTexParameteri(target, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
		glTexParameteri(target, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)

		return texId
	}

	fun draw(
		textureId: Int, texStride: Int, vertexStride: Int,
		coordsPerVertex: Int, texBuffer: glFloatBuffer, vertexBuffer: glFloatBuffer,
		target: Int = this.target, texMatrix: FloatArray = identityMatrix
	) {
		glUseProgram(handle)

		glActiveTexture(GL_TEXTURE0)
		glBindTexture(target, textureId)

		glUniformMatrix4fv(uTexMatrixLoc, 1, false, texMatrix, 0)

		glEnableVertexAttribArray(aPositionLoc)
		glVertexAttribPointer(aPositionLoc, coordsPerVertex, GL_FLOAT, false, vertexStride, vertexBuffer)

		glEnableVertexAttribArray(aTextureCoordLoc)
		glVertexAttribPointer(aTextureCoordLoc, coordsPerVertex, GL_FLOAT, false, texStride, texBuffer)

		glDrawArrays(GL_TRIANGLE_STRIP, 0, 4)
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