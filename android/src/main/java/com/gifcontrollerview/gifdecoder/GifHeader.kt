package com.gifcontrollerview.gifdecoder

import java.nio.ByteBuffer
import java.nio.ByteOrder

class GifHeader(bytes: ByteArray) {
    val buffer: ByteBuffer = ByteBuffer.allocate(HEADER_SIZE).apply {
        val byte = bytes.copyOfRange(0, HEADER_SIZE)
        this.put(byte)
        order(ByteOrder.LITTLE_ENDIAN)
    }

    val width: Int
        get() = buffer.getShort(WIDTH_POSITION).toInt()

    val height: Int
        get() = buffer.getShort(HEIGHT_POSITION).toInt()

    val globalColorTableFlag: Boolean
        get() = buffer.get(PACKED_FIELD_POSITION).toInt() and 0x80 == 0x80
    val colorResolution: Int
        get() = (buffer.get(PACKED_FIELD_POSITION).toInt() and 0x70 shr 4) + 1
    val sortFlag: Boolean
        get() = buffer.get(PACKED_FIELD_POSITION).toInt() and 0x08 == 0x08
    val sizeOfGlobalColorTable: Int
        get() = 2 shl (buffer.get(PACKED_FIELD_POSITION).toInt() and 0x07)
    val sizeOfGlobalColorTableInBytes: Int
        get() = 3 * sizeOfGlobalColorTable

    val backgroundColorIndex: Int
        get() = buffer.get(BACKGROUND_COLOR_INDEX_POSITION).toInt()
    val pixelAspectRatio: Int
        get() = buffer.get(PIXEL_ASPECT_RATIO_POSITION).toInt()

    fun getHex(): String {
        val hex = buffer.array().joinToString("") {
            String.format("%02X", it)
        }
        return hex
    }

    fun getHex(index: Int): String {
        val hex = String.format("%02X", buffer.get(index))
        return hex
    }

    fun getBinary(index: Int): String {
        val binary = String.format("%8s", Integer.toBinaryString(buffer.get(index).toInt() and 0xFF)).replace(' ', '0')
        return binary
    }

    companion object {
        const val HEADER_SIZE = 12
        const val WIDTH_POSITION = 6
        const val HEIGHT_POSITION = 8
        const val PACKED_FIELD_POSITION = 10
        const val BACKGROUND_COLOR_INDEX_POSITION = 11
        const val PIXEL_ASPECT_RATIO_POSITION = 12
    }
}