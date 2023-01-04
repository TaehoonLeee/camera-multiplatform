//
//  AppDelegate.swift
//  iosApp
//
//  Created by taehoon lee on 2023/01/04.
//  Copyright © 2023 orgName. All rights reserved.
//

import UIKit
import AVFoundation
import shared

@main
class AppDelegate: UIResponder, UIApplicationDelegate {
    
    var window: UIWindow?
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]?) -> Bool {
        window = UIWindow(frame: UIScreen.main.bounds)
        
        let previewControllerBuilder = PreviewControllerBuilder()
        let metalDevice: MTLDevice = MTLCreateSystemDefaultDevice()!
        let videoDevice: AVCaptureDevice = AVCaptureDevice.default(for: .video)!
        previewControllerBuilder.setMetalDevice(device: metalDevice)
        previewControllerBuilder.setVideoDevice(device: videoDevice)
        
        let videoSettings = [kCVPixelBufferPixelFormatTypeKey as String: Int(kCVPixelFormatType_32BGRA)]
        let viewController = previewControllerBuilder.create(videoSettings: videoSettings)
        window?.rootViewController = viewController
        window?.makeKeyAndVisible()
        
        return true
    }
    
}
