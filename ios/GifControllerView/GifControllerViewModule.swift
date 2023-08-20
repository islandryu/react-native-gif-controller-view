import Foundation
import React

@objc(GifControllerViewModule)
class GifControllerViewModule: NSObject, RCTBridgeModule {

  static func moduleName() -> String! {
    return "GifControllerViewModule"
  }
  var bridge: RCTBridge!

  @objc func getAllColorCount(_ viewTag: NSNumber,frameIndex: NSNumber,resolver: @escaping RCTPromiseResolveBlock, rejecter: @escaping RCTPromiseRejectBlock) {
      DispatchQueue.main.async {
        guard let gifView = self.bridge.uiManager.view(forReactTag: viewTag) as? GifControllerView else { return }
        let results = gifView.getAllColorCount(frameIndex.intValue)
        let array = results.map { (colorCount) -> NSDictionary in
          return ["color": self.CIColorToHex(colorCount.color), "count": colorCount.count]
        }
        resolver(array)
      }
  }
  @objc static func requiresMainQueueSetup() -> Bool {
      return true
  }

  func CIColorToHex(_ color: CIColor) -> String {
      let r = Int(color.red * 255)
      let g = Int(color.green * 255)
      let b = Int(color.blue * 255)
      return String(format: "#%02lX%02lX%02lX", r, g, b)
  }

  @objc func seekTo(_ viewTag: NSNumber,frameIndex: NSNumber, resolver: @escaping RCTPromiseResolveBlock, rejecter: @escaping RCTPromiseRejectBlock) {
      DispatchQueue.main.async {
        guard let gifView = self.bridge.uiManager.view(forReactTag: viewTag) as? GifControllerView else { return }
        do {
          try gifView.seekGif(to: frameIndex.intValue)
          resolver(nil)
        } catch {
          rejecter("error", "error", error)
        }
      }
  }
  
  @objc func getFrameData(_ viewTag: NSNumber, resolver: @escaping RCTPromiseResolveBlock, rejecter: @escaping RCTPromiseRejectBlock) {
      DispatchQueue.main.async {
        guard let gifView = self.bridge.uiManager.view(forReactTag: viewTag) as? GifControllerView else { return }
        let results = gifView.getFrameData()
        let array = results.map { (frameData) -> NSDictionary in
          return ["delayTime": frameData.delayTime, "frameIndex": frameData.frameIndex]
        }
        resolver(array)
      }
  }
}
