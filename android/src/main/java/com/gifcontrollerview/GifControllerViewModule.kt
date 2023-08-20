package com.gifcontrollerview

import android.util.Log
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.WritableArray
import com.facebook.react.bridge.WritableMap
import com.facebook.react.bridge.WritableNativeArray
import com.facebook.react.bridge.WritableNativeMap
import com.facebook.react.uimanager.UIManagerModule
import java.util.*

class GifControllerViewModule(reactContext: ReactApplicationContext): ReactContextBaseJavaModule(reactContext){
    override fun getName(): String {
        return "GifControllerViewModule"
    }
    @ReactMethod
    public fun getAllColorCount(viewId:Int, frameIndex:Int, promise: Promise) {
        val context = reactApplicationContext;
        val uiManager = context.getNativeModule(UIManagerModule::class.java)
        uiManager?.addUIBlock { nativeViewHierarchyManager ->
            val view = nativeViewHierarchyManager.resolveView(viewId) as? GifControllerView
            val result = view?.getAllColorCount(frameIndex)
            if (result != null) {
                val writableArray: WritableArray = WritableNativeArray()
                for (i in result) {
                    val map: WritableMap = WritableNativeMap()
                    map.putString("color", intToHexString(i.color))
                    map.putInt("count", i.count)
                    writableArray.pushMap(map)
                }
                promise.resolve(writableArray)
            } else {
                promise.reject("Error", "Could not get color count")
            }
        }
    }


    fun intToHexString(colorInt: Int): String {
        val red = (colorInt shr 16) and 0xFF
        val green = (colorInt shr 8) and 0xFF
        val blue = colorInt and 0xFF
        return String.format("#%02x%02x%02x", red, green, blue)
    }

    @ReactMethod
    public fun seekTo(viewId:Int, position:Int) {
        val context = reactApplicationContext;
        val uiManager = context.getNativeModule(UIManagerModule::class.java)
        uiManager?.addUIBlock { nativeViewHierarchyManager ->
            val view = nativeViewHierarchyManager.resolveView(viewId) as? GifControllerView
            view?.seekTo(position)
        }
    }

    @ReactMethod
    public fun getFrameData(viewId:Int, promise: Promise) {
        val context = reactApplicationContext;
        val uiManager = context.getNativeModule(UIManagerModule::class.java)
        uiManager?.addUIBlock { nativeViewHierarchyManager ->
            val view = nativeViewHierarchyManager.resolveView(viewId) as? GifControllerView
            val result = view?.getFrameData()
            if (result != null) {
                val writableArray: WritableArray = WritableNativeArray()
                for (i in result) {
                    val map: WritableMap = WritableNativeMap()
                    map.putInt("index", i.index)
                    map.putInt("delay", i.delay)
                    writableArray.pushMap(map)
                }
                promise.resolve(writableArray)
            } else {
                promise.reject("Error", "Could not get frame data")
            }
        }
    }
}
