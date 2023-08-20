package com.gifcontrollerview

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import com.gifcontrollerview.gifdecoder.GifDecoder
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.net.URL

class GifControllerView(context: Context, attrs: AttributeSet? = null) : androidx.appcompat.widget.AppCompatImageView(context, attrs) {
    private var gifDrawable: AnimatedGifDrawable? = null
    private var fromToColorPairs: List<AnimatedGifDrawable.ColorPair>? = null
    private var isAnimating: Boolean = true
    private var isReverse: Boolean = false
    private var speed: Float? = null

    fun setSource(source: String) {
        GlobalScope.launch(Dispatchers.Main) {
            val bytes = getUrlBytes(source)
            gifDrawable = bytes?.let { byteArray ->
                GifDecoder().apply { read(byteArray.inputStream()) }
            }?.let { decoder ->
                AnimatedGifDrawable(decoder)
            }
            setImageDrawable(gifDrawable)
            fromToColorPairs?.let { gifDrawable?.setFromToColorPairs(it) }
            gifDrawable?.isReverse = isReverse
            gifDrawable?.speed = speed ?: 1f
            if (isAnimating) {
                gifDrawable?.start()
            } else {
                gifDrawable?.stop()
            }
        }
    }

    private suspend fun getUrlBytes(url: String): ByteArray? {
        return withContext(Dispatchers.IO) {
            URL(url).openStream().use { stream ->
                val buffer = ByteArray(1024)
                val output = ByteArrayOutputStream()
                var bytesRead = stream.read(buffer)
                while (bytesRead != -1) {
                    output.write(buffer, 0, bytesRead)
                    bytesRead = stream.read(buffer)
                }
                output.toByteArray()
            }
        }
    }

    public fun start() {
        this.isAnimating = true
        gifDrawable?.start()
    }
    public fun stop() {
        this.isAnimating = false
        gifDrawable?.stop()
    }

    public fun setFromToColorPairs(pairs: List<AnimatedGifDrawable.ColorPair>) {
        this.fromToColorPairs = pairs
        gifDrawable?.setFromToColorPairs(pairs)
    }

    public fun setIsReverse(isReverse: Boolean) {
        this.isReverse = isReverse
        gifDrawable?.isReverse = isReverse
    }

    public fun setSpeed(speed: Float) {
        this.speed = speed
        gifDrawable?.speed = speed
    }

    public fun getAllColorCount(frameIndex: Int): List<AnimatedGifDrawable.ColorCount>? {
        return gifDrawable?.getAllColorCount(frameIndex)
    }
    public fun seekTo(position: Int) {
        gifDrawable?.seekTo(position)
    }

    public fun getFrameData(): List<AnimatedGifDrawable.FrameData>? {
        return gifDrawable?.getFrameData()
    }
}