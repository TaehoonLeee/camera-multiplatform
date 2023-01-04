//
//  passthrough.metal
//  iosApp
//
//  Created by taehoon lee on 2023/01/04.
//  Copyright © 2023 orgName. All rights reserved.
//

#include <metal_stdlib>
using namespace metal;


kernel void bypassKernel(texture2d<float, access::read> inTexture [[ texture(0) ]],
                              texture2d<float, access::write> outTexture [[ texture(1) ]],
                              uint2 gid [[ thread_position_in_grid ]]) {
    float4 originalColor = inTexture.read(gid);
    outTexture.write(originalColor, gid);
}
