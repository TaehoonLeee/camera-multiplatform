package com.example.camera.gles.program

import com.example.camera.gles.api.*
import com.example.camera.resources.imageResources

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
			
			uniform int uLut;
			uniform sampler2D lookupTable;

			void main() {
				vec4 textureColor = texture2D(sTexture, vTextureCoord);
				
				if (uLut == 1) {
					textureColor = clamp(textureColor, 0.0, 1.0);
					mediump float blueColor = textureColor.b * 63.0;

					mediump vec2 quad1;
					quad1.y = floor(floor(blueColor) / 8.0);
					quad1.x = floor(blueColor) - (quad1.y * 8.0);

					mediump vec2 quad2;
					quad2.y = floor(ceil(blueColor) / 8.0);
					quad2.x = ceil(blueColor) - (quad2.y * 8.0);

					highp vec2 texPos1;
					texPos1.x = (quad1.x * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.r);
					texPos1.y = (quad1.y * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.g);

					highp vec2 texPos2;
					texPos2.x = (quad2.x * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.r);
					texPos2.y = (quad2.y * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.g);

					lowp vec4 newColor1 = texture2D(lookupTable, texPos1);
					lowp vec4 newColor2 = texture2D(lookupTable, texPos2);

					gl_FragColor = mix(newColor1, newColor2, fract(blueColor));
				} else {
					gl_FragColor = textureColor;
				}
			}
		""".trimIndent()
	}

	private val target: Int
	private val handle: Int

	private val aPositionLoc: Int
	private val aTextureCoordLoc: Int
	private val uTexMatrixLoc: Int

	private val uLutLoc: Int
	private val lookupTable: Int
	private val lookupTableLoc: Int

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

		uLutLoc = glGetUniformLocation(handle, "uLut")
		lookupTableLoc = glGetUniformLocation(handle, "lookupTable")
		lookupTable = createImageTextures(imageResources("sample_clut.png"), 512, 512)
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

	fun createImageTextures(data: ByteArray, width: Int, height: Int): Int {
		val textures = intArrayOf(0)
		glGenTextures(1, textures)

		val texId = textures[0]
		glBindTexture(target, texId)
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)

		glTexImage2D(GL_TEXTURE_2D, width, height, data)

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

		glUniform1i(uLutLoc, 1)
		glActiveTexture(GL_TEXTURE1)
		glBindTexture(GL_TEXTURE_2D, lookupTable)
		glUniform1i(lookupTableLoc, 1)

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