# camera-multiplatform

Multiplatform camera rendering in Android, iOS by using OpenGL ES

#### Android Specs
- Rendering Camera Graphics Buffer with [OpenGL ES](https://developer.android.com/guide/topics/graphics/opengl?hl=ko), [EGL](https://www.khronos.org/egl)
- Get Camera Buffer from [Camera2 Api](https://developer.android.com/training/camera2)
- Offer Surface from [SurfaceView](https://developer.android.com/reference/android/view/SurfaceView?hl=ko)

#### iOS Specs
- Rendering Camera Graphics Buffer with [OpenGL ES](https://developer.apple.com/documentation/opengles), [EAGL](https://developer.apple.com/documentation/opengles/eaglcontext) (Metal later)
- Get Camera Buffer from [AVCaptureDevice](https://developer.apple.com/documentation/avfoundation/avcapturedevice)
- Use [GLKView](https://developer.apple.com/documentation/glkit/glkview) to render OpenGL ES content

## Features
- [x] Camera Preview in Android, iOS
- [ ] Commonize Rendering Code
- [ ] Filtering Camera Preview
- [ ] Recording Camera
