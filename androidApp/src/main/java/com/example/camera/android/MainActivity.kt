package com.example.camera.android

import android.Manifest
import android.app.Activity
import android.os.Bundle

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.act_main)

        checkSelfPermission(Manifest.permission.CAMERA)
        requestPermissions(arrayOf(Manifest.permission.CAMERA), 1234)
    }
}