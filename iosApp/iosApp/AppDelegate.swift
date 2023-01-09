//
//  AppDelegate.swift
//  iosApp
//
//  Created by taehoon lee on 2023/01/04.
//  Copyright © 2023 orgName. All rights reserved.
//

import UIKit
import GLKit
import shared
import AVFoundation

let VERTEX_SHADER =
    "attribute vec4 a_position;\n" +
        "attribute vec4 aPosition;\n" +
        "attribute vec4 aTextureCoord;\n" +
        "varying vec2 vTextureCoord;\n" +
        "void main()\n" +
        "{\n" +
        "    gl_Position = aPosition;\n" +
        "    vTextureCoord = aTextureCoord.xy;             \n" +
        "}\n"

let FRAGMENT_SHADER =
    "precision mediump float;\n" +
    "varying vec2 vTextureCoord;\n" +
    "uniform sampler2D sTexture;\n" +
    "void main() {\n" +
    "   gl_FragColor = texture2D(sTexture, vTextureCoord);\n" +
    "}\n"

@main
class AppDelegate: UIResponder, UIApplicationDelegate {
    
    var window: UIWindow?
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]?) -> Bool {
//        window = UIWindow(frame: UIScreen.main.bounds)
//        window?.rootViewController = PreviewKt.createPreviewController()
//        window?.makeKeyAndVisible()
        let camera = Device()
        let cameraViewController = CameraViewController()
        window = UIWindow(frame: UIScreen.main.bounds)
        window?.rootViewController = cameraViewController
        window?.makeKeyAndVisible()

        camera.setOutput(delegate: cameraViewController)

        return true
    }
}

class CameraViewController : GLKViewController, AVCaptureVideoDataOutputSampleBufferDelegate {
    
    private var program: GLuint = 0
    private var textureCache: CVOpenGLESTextureCache?

    private var aPositionLoc = GLuint(0)
    private var aTextureCoordLoc = GLuint(0)

    private let VERTEX_BUF: [Float] = [-1, -1, 1, -1, -1, 1, 1, 1]
    private let TEX_BUF: [Float] = [0, 0, 1, 0, 0, 1, 1, 1]
    
    override func viewDidLoad() {
        guard let _context = EAGLContext(api: .openGLES3) else { return print("Failed to create ES Context") }
        EAGLContext.setCurrent(_context)
       
        let view = self.view as! GLKView
        view.context = _context

        CVOpenGLESTextureCacheCreate(kCFAllocatorDefault, nil, _context, nil, &textureCache)
        createResources()
    }

    override func glkView(_ view: GLKView, drawIn rect: CGRect) {
        glDrawArrays(GLenum(GL_TRIANGLE_STRIP), 0, 4)
    }
    
    func captureOutput(_ output: AVCaptureOutput, didOutput sampleBuffer: CMSampleBuffer, from connection: AVCaptureConnection) {
        guard let imageBuffer = CMSampleBufferGetImageBuffer(sampleBuffer) else { return }
        let width = CVPixelBufferGetWidth(imageBuffer)
        let height = CVPixelBufferGetHeight(imageBuffer)

        CVOpenGLESTextureCacheFlush(textureCache!, 0)

        var cvTexture: CVOpenGLESTexture?
        CVOpenGLESTextureCacheCreateTextureFromImage(kCFAllocatorDefault, textureCache!, imageBuffer, nil, GLenum(GL_TEXTURE_2D), GL_RGBA, GLsizei(width), GLsizei(height), GLenum(GL_BGRA), GLenum(GL_UNSIGNED_BYTE), 0, &cvTexture)

        DispatchQueue.main.async { [self] in
            glActiveTexture(GLenum(GL_TEXTURE0))
            glBindTexture(CVOpenGLESTextureGetTarget(cvTexture!), CVOpenGLESTextureGetName(cvTexture!))
            glTexParameteri(GLenum(GL_TEXTURE_2D), GLenum(GL_TEXTURE_MIN_FILTER), GL_LINEAR)
            glTexParameteri(GLenum(GL_TEXTURE_2D), GLenum(GL_TEXTURE_MAG_FILTER), GL_LINEAR)
            glTexParameterf(GLenum(GL_TEXTURE_2D), GLenum(GL_TEXTURE_WRAP_S), GLfloat(GL_CLAMP_TO_EDGE))
            glTexParameterf(GLenum(GL_TEXTURE_2D), GLenum(GL_TEXTURE_WRAP_T), GLfloat(GL_CLAMP_TO_EDGE))
            
            glEnableVertexAttribArray(aPositionLoc)
            glVertexAttribPointer(aPositionLoc, 2, GLenum(GL_FLOAT), GLboolean(GL_FALSE), 8, VERTEX_BUF)

            glEnableVertexAttribArray(aTextureCoordLoc)
            glVertexAttribPointer(aTextureCoordLoc, 2, GLenum(GL_FLOAT), GLboolean(GL_FALSE), 8, TEX_BUF)
        }
    }
    
    private func createResources() {
        program = glCreateProgram()
        let vertexShader = createShader(type: GLenum(GL_VERTEX_SHADER), source: VERTEX_SHADER as NSString)
        let fragmentShader = createShader(type: GLenum(GL_FRAGMENT_SHADER), source: FRAGMENT_SHADER as NSString)
        
        glAttachShader(program, vertexShader)
        glAttachShader(program, fragmentShader)
        
        glLinkProgram(program)

        aPositionLoc = GLuint(glGetAttribLocation(program, "aPosition"))
        aTextureCoordLoc = GLuint(glGetAttribLocation(program, "aTextureCoord"))
        
        glUseProgram(program)
    }

    private func createShader(type: GLenum, source: NSString) -> GLuint {
        let shader = glCreateShader(type)
        var sourcePointer = UnsafePointer<GLchar>(source.utf8String)
        glShaderSource(shader, 1, &sourcePointer, nil)
        glCompileShader(shader)

        return shader
    }
}
