package com.gifcontrollerview.gifdecoder

import java.io.InputStream
import java.nio.ByteBuffer

fun ByteBuffer.toByteArray(length: Int): ByteArray {
    if (this.remaining() >= length) {
        val byteArray = ByteArray(length)

        this.get(byteArray, 0, length)

        return byteArray
    } else {
        throw IllegalArgumentException("The specified length is invalid.")
    }
}

class GifDecoder {
    lateinit var inputStream: InputStream
    lateinit var header: GifHeader
     var globalColorTable: ColorTable? = null
    lateinit var bytes: ByteArray
    var frames = mutableListOf<GifFrame>()
    val HEADER_SIZE = 13
    fun read(inputStream: InputStream): GifDecoder {
        this.inputStream = inputStream
        bytes = inputStream.readBytes()
        val byteBuffer = ByteBuffer.wrap(bytes)
        val headerByteArray = byteBuffer.toByteArray(HEADER_SIZE)
        header = GifHeader(headerByteArray)
        recursiveCheckApplicationExtension(byteBuffer)
        if(header.globalColorTableFlag) {
            val globalTableByteArray = byteBuffer.toByteArray(header.sizeOfGlobalColorTableInBytes)
            globalColorTable = ColorTable(globalTableByteArray, header.sizeOfGlobalColorTableInBytes / 3)
        }
        recursiveCheckApplicationExtension(byteBuffer)
        var index = 0
        while (
            byteBuffer.hasRemaining()
        ) {
            val frame = GifFrame(byteBuffer, header)
            if(frame.frame == null) {
                break
            }
            frames.add(frame)
            index++
        }
        return this
    }

    val frameCount: Int
        get() = frames.size

    val width: Int
        get() = header.width
    val height: Int
        get() = header.height

    data class Color(val red: Int, val green: Int, val blue: Int)
}


