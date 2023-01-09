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
import shared

@main
class AppDelegate: UIResponder, UIApplicationDelegate {
    
    var window: UIWindow?
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]?) -> Bool {
        window = UIWindow(frame: UIScreen.main.bounds)
        window?.rootViewController = PreviewKt.createPreviewController()
        window?.makeKeyAndVisible()

        return true
    }
}
