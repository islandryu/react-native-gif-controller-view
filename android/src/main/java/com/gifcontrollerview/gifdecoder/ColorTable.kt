package com.gifcontrollerview.gifdecoder

import java.nio.ByteBuffer

class ColorTable(byteArray: ByteArray, size: Int) {
    private val buffer: ByteBuffer = ByteBuffer.wrap(byteArray)
    private val size: Int = size

    fun getColor(index: Int): GifDecoder.Color {
        if (index < 0 || index >= size) {
            throw IllegalArgumentException("Invalid color table index: $index")
        }

        val colorPosition = index * 3
        val red = buffer.get(colorPosition).toInt() and 0xFF
        val green = buffer.get(colorPosition + 1).toInt() and 0xFF
        val blue = buffer.get(colorPosition + 2).toInt() and 0xFF

        return GifDecoder.Color(red, green, blue)
    }
}
