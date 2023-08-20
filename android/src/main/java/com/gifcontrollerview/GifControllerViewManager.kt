package com.gifcontrollerview

import android.graphics.Color
import android.util.Log
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.annotations.ReactProp

class GifControllerViewManager : SimpleViewManager<GifControllerView>() {
    companion object {
        const val REACT_CLASS = "GifControllerView"
    }

    override fun getName(): String {
        return REACT_CLASS
    }

    override fun createViewInstance(context: ThemedReactContext): GifControllerView {
        return GifControllerView(context)
    }

    @ReactProp(name = "source")
    fun setSource(view: GifControllerView, source: String) {
        view.setSource(source)
    }

    @ReactProp(name = "colorMappings")
    fun setColorMappings(view: GifControllerView, colorPairsArray: ReadableArray) {
        val colorPairs = mutableListOf<AnimatedGifDrawable.ColorPair>()
        for (i in 0 until colorPairsArray.size()) {
            val pairMap = colorPairsArray.getMap(i)
            val fromColor = pairMap.getInt("from")
            val toColor = pairMap.getInt("to")
            if(fromColor == null || toColor == null) continue
            colorPairs.add(AnimatedGifDrawable.ColorPair(fromColor, toColor))
        }

        view.setFromToColorPairs(colorPairs) // Assuming your GifControllerView has this method
    }

    fun hexStringToInt(hexString: String): Int {
        // Remove the hash at the start if it's there
        val cleanedHexString = if (hexString.startsWith("#")) hexString.substring(1) else hexString
        // Convert to integer
        return cleanedHexString.toInt(16)
    }

    @ReactProp(name = "isAnimating")
    fun setIsAnimating(view: GifControllerView, isAnimating: Boolean) {
        if (isAnimating) {
            view.start()
        } else {
            view.stop()
        }
    }
    
    @ReactProp(name = "isReverse")
    fun setIsReverse(view: GifControllerView, isReverse: Boolean) {
        view.setIsReverse(isReverse)
    }

    @ReactProp(name = "speed")
    fun setSpeed(view: GifControllerView, speed: Float) {
        view.setSpeed(1 / speed)
    }
}
