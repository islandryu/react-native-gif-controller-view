#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(GifControllerViewModule, NSObject)

RCT_EXTERN_METHOD(getAllColorCount:(nonnull NSNumber *)viewTag
                  frameIndex:(nonnull NSNumber *)frameIndex
                  resolver:(RCTPromiseResolveBlock)resolver
                  rejecter:(RCTPromiseRejectBlock)rejecter)

RCT_EXTERN_METHOD(seekTo:(nonnull NSNumber *)viewTag
                  frameIndex:(nonnull NSNumber *)frameIndex
                  resolver:(RCTPromiseResolveBlock)resolver
                  rejecter:(RCTPromiseRejectBlock)rejecter)

RCT_EXTERN_METHOD(getFrameData:(nonnull NSNumber *)viewTag
                  resolver:(RCTPromiseResolveBlock)resolver
                  rejecter:(RCTPromiseRejectBlock)rejecter)

@end
