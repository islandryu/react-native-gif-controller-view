#include <metal_stdlib>
using namespace metal;
#include <CoreImage/CoreImage.h>

extern "C" {
    namespace coreimage {
        half4 colorTransform_kernel(sample_h s, half4 from, half4 to) {
            half4 pixel = s.rgba;
            if (all(abs(pixel - from) <= 0.0001)) {
                return premultiply(to);
            } else {
                return premultiply(pixel);
            }
        }
    }
}
