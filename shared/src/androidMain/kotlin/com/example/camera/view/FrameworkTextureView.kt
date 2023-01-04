package com.example.camera.view

import android.content.Context
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

//actual class FrameworkTextureView(context: Context) : GLSurfaceView(context) {
//
//	private lateinit var viewPort: Pair<Int, Int>
//	private val directContext = DirectContext.makeGL()
//
//	init {
//		setEGLContextClientVersion(2)
//		setRenderer(Renderer())
//	}
//
//	private inner class Renderer : GLSurfaceView.Renderer {
//		override fun onSurfaceCreated(gl: GL10, config: EGLConfig?) {
//			TODO("Not yet implemented")
//		}
//
//		override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
//			viewPort = width to height
//		}
//
//		override fun onDrawFrame(gl: GL10) {
//			val backendRenderTarget = BackendRenderTarget.makeGL(
//				fbId = 0,
//				sampleCnt = 0,
//				stencilBits = 8,
//				width = viewPort.first,
//				height = viewPort.second,
//				fbFormat = FramebufferFormat.GR_GL_RGBA8
//			)
//			val renderTarget = Surface.makeFromBackendRenderTarget(
//				colorSpace = null,
//				context = directContext,
//				rt = backendRenderTarget,
//				origin = SurfaceOrigin.BOTTOM_LEFT,
//				colorFormat = SurfaceColorFormat.RGBA_8888,
//				surfaceProps = SurfaceProps(PixelGeometry.UNKNOWN)
//			)?: return
//
//			val canvas = renderTarget.canvas
//			canvas.drawRect(Rect.makeXYWH(-90.5f, -90.5f, 181f, 181f), Paint().apply { color = Color.GREEN })
//			canvas.restore()
//			canvas.close()
//			directContext.flush()
//		}
//	}
//}