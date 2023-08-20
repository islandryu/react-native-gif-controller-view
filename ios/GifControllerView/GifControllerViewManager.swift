import Foundation
import React

@objc(GifControllerViewManager)
class GifControllerViewManager: RCTViewManager {
    override static func moduleName() -> String {
        return "GifControllerView"
    }

    override func view() -> UIView {
        return GifControllerView()
    }

    @objc override static func requiresMainQueueSetup() -> Bool {
        return false
    }
   
}
