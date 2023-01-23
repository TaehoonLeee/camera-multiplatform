# camera-multiplatform

Inspired by [Grafika](https://github.com/google/grafika), [Camera2 Sample](https://github.com/android/camera-samples/tree/main/Camera2Video), [Kotlinconf-Spinner](https://github.com/Kotlin/kotlinconf-spinner), [Filtered Shader](https://github.com/mattdesl/glsl-lut/blob/master/example/index.js)

Multiplatform camera rendering in Android, iOS by using OpenGL ES

#### Android Specs
- Rendering Camera Graphics Buffer with [OpenGL ES](https://developer.android.com/guide/topics/graphics/opengl?hl=ko), [EGL](https://www.khronos.org/egl)
- Get Camera Buffer from [Camera2 Api](https://developer.android.com/training/camera2)
- Get Surface from [SurfaceView](https://developer.android.com/reference/android/view/SurfaceView?hl=ko) to render OpenGL ES content

#### iOS Specs
- Rendering Camera Graphics Buffer with [OpenGL ES](https://developer.apple.com/documentation/opengles), [EAGL](https://developer.apple.com/documentation/opengles/eaglcontext) (Metal later)
- Get Camera Buffer from [AVCaptureDevice](https://developer.apple.com/documentation/avfoundation/avcapturedevice)
- Use [GLKView](https://developer.apple.com/documentation/glkit/glkview) to render OpenGL ES content

## Features
- [x] Camera Preview in Android, iOS
- [x] Commonize Rendering Code
- [ ] Filtering Camera Preview
- [ ] Recording Camera
