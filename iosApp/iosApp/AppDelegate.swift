//
//  AppDelegate.swift
//  iosApp
//
//  Created by taehoon lee on 2023/01/04.
//  Copyright © 2023 orgName. All rights reserved.
//

import UIKit
import AVFoundation
import MetalKit

@main
class AppDelegate: UIResponder, UIApplicationDelegate {
    
    var window: UIWindow?
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]?) -> Bool {
        
        window = UIWindow(frame: UIScreen.main.bounds)
        window?.rootViewController = PreviewController()
        window?.makeKeyAndVisible()
        
//        let previewControllerBuilder = PreviewControllerBuilder()
//        let metalDevice: MTLDevice = MTLCreateSystemDefaultDevice()!
//        let videoDevice: AVCaptureDevice = AVCaptureDevice.default(for: .video)!
//        previewControllerBuilder.setMetalDevice(device: metalDevice)
//        previewControllerBuilder.setVideoDevice(device: videoDevice)
//
//        let videoSettings = [kCVPixelBufferPixelFormatTypeKey as String: Int(kCVPixelFormatType_32BGRA)]
//        let viewController = previewControllerBuilder.create(videoSettings: videoSettings)
//        window?.rootViewController = viewController
//        window?.makeKeyAndVisible()
        
        return true
    }
}

class PreviewController: UIViewController, AVCaptureVideoDataOutputSampleBufferDelegate {
    
    private var preview: Preview!
    private var camera: Camera!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        camera = Camera()
        preview = Preview(frame: UIScreen.main.bounds, device: MTLCreateSystemDefaultDevice())
        view.addSubview(preview)
        camera.setOutput(delegate: self)
    }
    
    func captureOutput(_ output: AVCaptureOutput, didOutput sampleBuffer: CMSampleBuffer, from connection: AVCaptureConnection) {
        DispatchQueue.main.async {
            self.preview.pixelBuffer = CMSampleBufferGetImageBuffer(sampleBuffer)
        }
    }
}

class Camera {
    
    private let camera = AVCaptureDevice.default(for: .video)!
    private let captureSession = AVCaptureSession()
    private let videoOutput = AVCaptureVideoDataOutput()
    private let queue = DispatchQueue(label: "camera-queue")
    
    public func setOutput(delegate: AVCaptureVideoDataOutputSampleBufferDelegate) {
        captureSession.sessionPreset = .hd1920x1080
        
        guard let input = try? AVCaptureDeviceInput(device: camera) else { return }
        
        if captureSession.canAddInput(input) {
            captureSession.addInput(input)
        }
        
        videoOutput.videoSettings = [kCVPixelBufferPixelFormatTypeKey as String: NSNumber(value: kCVPixelFormatType_32BGRA)]
        videoOutput.alwaysDiscardsLateVideoFrames = true
        videoOutput.setSampleBufferDelegate(delegate, queue: queue)
        if captureSession.canAddOutput(videoOutput) {
            captureSession.addOutput(videoOutput)
        }
        
        videoOutput.connection(with: AVMediaType.video)?.videoOrientation = .portrait
        captureSession.startRunning()
    }    
}

class Preview : MTKView {
    
    private let DEFAULT_IMAGE_VERTICES: [Float] = [-1, 1, 1, 1, -1, -1, 1, -1]
    private let DEFAULT_TEXTURE_COORDINATE: [Float] = [0, 0, 1, 0, 0, 1, 1, 1]
    
    var pixelBuffer: CVPixelBuffer? {
        didSet {
            setNeedsDisplay()
        }
    }
    
    private var commandQueue: MTLCommandQueue
    private var textureCache: CVMetalTextureCache?
    private var pipelineState: MTLComputePipelineState
    
    required override init(frame frameRect: CGRect, device: MTLDevice?) {
        pixelBuffer = nil
        commandQueue = device!.makeCommandQueue()!
        
        do {
            let library = try device!.makeDefaultLibrary(bundle: .main)
            let kernelFunction = library.makeFunction(name: "bypassKernel")!
            pipelineState = try device!.makeComputePipelineState(function: kernelFunction)
        } catch {
            fatalError()
        }
        
        super.init(frame: frameRect, device: device)
        
        self.device = device
        self.framebufferOnly = false
        CVMetalTextureCacheCreate(kCFAllocatorDefault, nil, device!, nil, &textureCache)
    }
    
    required init(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func draw(_ rect: CGRect) {
        autoreleasepool {
            if !rect.isEmpty {
                render()
            }
        }
    }
    
    private func render() {
        guard let imageBuffer = pixelBuffer else { return }
        let width = CVPixelBufferGetWidth(imageBuffer)
        let height = CVPixelBufferGetHeight(imageBuffer)
        
        var cvTexture: CVMetalTexture?
        CVMetalTextureCacheCreateTextureFromImage(kCFAllocatorDefault, textureCache!, imageBuffer, nil, .bgra8Unorm, width, height, 0, &cvTexture)
        
        guard let drawable = currentDrawable else { return }
        guard let texture = CVMetalTextureGetTexture(cvTexture!) else { return }
        if let commandBuffer = commandQueue.makeCommandBuffer(), let computeCommandEncoder = commandBuffer.makeComputeCommandEncoder() {
            computeCommandEncoder.setComputePipelineState(pipelineState)
            computeCommandEncoder.setTexture(texture, index: 0)
            computeCommandEncoder.setTexture(drawable.texture, index: 1)
            computeCommandEncoder.dispatchThreadgroups(texture.threadGroups(), threadsPerThreadgroup: texture.threadGroupCount())
            computeCommandEncoder.endEncoding()
            
            commandBuffer.present(drawable)
            commandBuffer.commit()
        }
    }
}

extension MTLTexture {
    
    func threadGroupCount() -> MTLSize {
        return MTLSizeMake(8, 8, 1)
    }
    
    func threadGroups() -> MTLSize {
        let groupCount = threadGroupCount()
        return MTLSize(width: (Int(width) + groupCount.width-1)/groupCount.width,
                       height: (Int(height) + groupCount.height-1)/groupCount.height,
                       depth: 1)
    }
}
