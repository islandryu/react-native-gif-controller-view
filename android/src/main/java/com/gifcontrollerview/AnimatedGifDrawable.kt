package com.gifcontrollerview

import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.Log
import com.gifcontrollerview.gifdecoder.GifDecoder
import java.util.concurrent.ScheduledThreadPoolExecutor

class AnimatedGifDrawable constructor(private val gifDecoder: GifDecoder) : Drawable() {
    private val framePaint = Paint()
    private var currentFrameIndex = 0
    private var FromToColorPairs: List<ColorPair>? = null
    class ColorPair(val from: Int, val to: Int)
    var isReverse = false
    var speed = 1.0f

    private var  isRunning = true

    private var bitmapArray = arrayOfNulls<Bitmap>(gifDecoder.frameCount)
    init {
        var bitmap: Bitmap? = null
        gifDecoder.frames.forEachIndexed { index, frame ->
            bitmap = frame.getBitmap(gifDecoder.globalColorTable, bitmap?.copy(Bitmap.Config.ARGB_8888, true))
            bitmapArray[index] = bitmap
        }
    }

    fun start() {
        if (isRunning) {
            return
        }
        isRunning = true
        scheduleNextFrame()
    }

    fun stop() {
        if (!isRunning) {
            return
        }
        isRunning = false
    }

    init {
        framePaint.isFilterBitmap = true
    }
    private val executor = ScheduledThreadPoolExecutor(1)

    override fun draw(canvas: Canvas) {
        var bitmap = bitmapArray[currentFrameIndex]
        if (bitmap != null) {
            val bounds = Rect(0, 0, canvas.width, canvas.height)
            bitmap = preDrawBitmap(bitmap)
            canvas.drawBitmap(bitmap, null, bounds, framePaint)
        }
        val delay = gifDecoder.frames[currentFrameIndex].graphicControlExtension?.delayTime ?: 10
        if(isRunning) {
            executor.schedule(updater, (delay.toFloat()*10f*speed).toLong(), java.util.concurrent.TimeUnit.MILLISECONDS)

        }
    }

    override fun setAlpha(alpha: Int) {
        framePaint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        framePaint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSPARENT
    }


    private val updater = Runnable {
        scheduleNextFrame()
    }

    private fun scheduleNextFrame() {
        if(isReverse) {
            currentFrameIndex = (currentFrameIndex - 1 + gifDecoder.frameCount) % gifDecoder.frameCount
        } else {
            currentFrameIndex = (currentFrameIndex + 1) % gifDecoder.frameCount
        }
        invalidateSelf()
    }

    private fun preDrawBitmap(bitmap: Bitmap): Bitmap {
        var result = bitmap
        FromToColorPairs?.let {
            for (colorPair in it) {
                result = changeBitmapColor(result, colorPair.from, colorPair.to)
            }
        }
        return result
    }

    fun changeBitmapColor(sourceBitmap: Bitmap, fromColor: Int, toColor: Int): Bitmap {
        val resultBitmap = sourceBitmap.copy(Bitmap.Config.ARGB_8888, true)
        for(y in 0 until sourceBitmap.height) {
            for(x in 0 until sourceBitmap.width) {
                val pixelColor = resultBitmap.getPixel(x, y)
                if(pixelColor == fromColor) {
                    resultBitmap.setPixel(x, y, toColor)
                }
            }
        }
        return resultBitmap
    }

    fun setFromToColorPairs(FromToColorPairs: List<ColorPair>) {
        this.FromToColorPairs = FromToColorPairs
    }

    fun getAllColorCount(index: Int?): List<ColorCount> {
        val result = mutableListOf<ColorCount>()
        val bitmap = bitmapArray[index ?: currentFrameIndex]
        if (bitmap != null) {
            for(y in 0 until bitmap.height) {
                for(x in 0 until bitmap.width) {
                    val pixelColor = bitmap.getPixel(x, y)
                    val index = result.indexOfFirst { it.color == pixelColor }
                    if(index == -1) {
                        result.add(ColorCount(pixelColor, 1))
                    } else {
                        result[index] = ColorCount(pixelColor, result[index].count + 1)
                    }
                }
            }
        }
        return result
    }

    fun seekTo(position: Int) {
        currentFrameIndex = position
        invalidateSelf()
    }

    // list frame data
    fun getFrameData(): List<FrameData> {
        val result = mutableListOf<FrameData>()
        for (i in 0 until gifDecoder.frameCount) {
            val frame = gifDecoder.frames[i]
            val delay = frame.graphicControlExtension?.delayTime ?: 10
            val frameData = FrameData(i, delay)
            result.add(frameData)
        }
        return result
    }

    data class ColorCount(val color: Int, val count: Int)
    data class FrameData(val index: Int, val delay: Int)
}
