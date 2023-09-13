#import <Foundation/Foundation.h>
#import <React/RCTViewManager.h>

@interface RCT_EXTERN_MODULE(GifControllerViewManager, RCTViewManager)

RCT_EXPORT_VIEW_PROPERTY(source, NSString)
RCT_EXPORT_VIEW_PROPERTY(colorMappings, NSArray)
RCT_EXPORT_VIEW_PROPERTY(isAnimating, BOOL)
RCT_EXPORT_VIEW_PROPERTY(isReverse, BOOL)
RCT_EXPORT_VIEW_PROPERTY(speed, NSNumber)
RCT_EXPORT_VIEW_PROPERTY(disableLoop, BOOL)

@end


