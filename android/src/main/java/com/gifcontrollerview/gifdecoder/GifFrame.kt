package com.gifcontrollerview.gifdecoder

import android.graphics.Bitmap
import android.graphics.Color
import com.gifcontrollerview.gifdecoder.recursiveCheckApplicationExtension
import java.nio.ByteBuffer
import java.nio.ByteOrder

class GifFrame(
    byteBuffer: ByteBuffer,
    header: GifHeader,
) {
    private val header: GifHeader = header
    private val byteBuffer = byteBuffer
    private var localColorTable:  ColorTable? = null

    var graphicControlExtension: GraphicControlExtension? = null
    private var applicationExtension: ApplicationExtension? = null
    private var imageDescriptor: ImageDescriptor? = null
    var frame: ByteArray? = null

    init {
        getExtensions()
        val a = 0
    }

    private fun getExtensions() {
        while (byteBuffer.hasRemaining()){
            val byte = byteBuffer.get()
            val byteInt = byte.toInt() and 0xff
            if(byteInt == 0x21) {
                val extensionInt = byteBuffer.get().toInt() and 0xff
                if(extensionInt == 0xFF) {
                    recursiveCheckApplicationExtension(byteBuffer)
                }
                else if(extensionInt == 0xF9) {
                    graphicControlExtension = getGraphicControlExtension(byteBuffer)
                }
            }
            else if (byteInt == 0x2c) {
                imageDescriptor = getImageDescriptor(byteBuffer)
                var colorTable: ColorTable? = null
                var width = header.width
                var height = header.height
                imageDescriptor?.let{
                    if(it.localColorTableFlag){
                        val colorTableSize = it.sizeOfLocalColorTable
                        localColorTable = ColorTable(byteBuffer.toByteArray(colorTableSize * 3), colorTableSize)
                    }
                    width = it.width
                    height = it.height
                }
                val minCodeSize = byteBuffer.get().toInt() and 0xFF
                val size = byteBuffer.get().toInt() and 0xFF
                val npix = width * height
                val data = lzw(minCodeSize, byteBuffer, npix, size)
                frame = data
                return
            }
        }
    }

    private fun getByteArray(byteBuffer: ByteBuffer, size: Int): ByteArray {
        val byteArray = ByteArray(size)
        byteBuffer.get(byteArray)
        return byteArray
    }

    private fun getGraphicControlExtension(byteBuffer: ByteBuffer): GraphicControlExtension {
        val byteSize = byteBuffer.get()
        val packageFields = byteBuffer.get().toInt() and 0xFF
        val delayTime  = byteBuffer.order(ByteOrder.LITTLE_ENDIAN).short.toInt() and 0xFFFF
        val transparentColorIndex = byteBuffer.get().toInt() and 0xFF
        val packageFieldInteger = packageFields.toInt()
        val disposalMethod = ((packageFieldInteger and 0b00011100) shr 2).toInt() and 0xFF
        val userInputFlag = (packageFieldInteger and 0b00000010) shr 1 == 1
        val transparentColorFlag = packageFieldInteger and 0b00000001 == 1

        return GraphicControlExtension(disposalMethod, userInputFlag, transparentColorFlag, delayTime, transparentColorIndex)
    }

    private fun getImageDescriptor(byteBuffer: ByteBuffer): ImageDescriptor {
        val left = byteBuffer.short.toInt() and 0xFFFF
        val top = byteBuffer.short.toInt()and 0xFFFF
        val width = byteBuffer.short.toInt()and 0xFFFF
        val height = byteBuffer.short.toInt()and 0xFFFF
        val packedField = byteBuffer.get().toInt()and 0xFFFF
        val localColorTableFlag = packedField and 0x80 == 0x80
        val interlaceFlag = packedField and 0x40 == 0x40
        val sortFlag = packedField and 0x20 == 0x20
        val sizeOfLocalColorTable = 2 shl (packedField and 0x07)
        val sizeOfLocalColorTableInBytes = 3 * sizeOfLocalColorTable

        return ImageDescriptor(left, top, width, height, localColorTableFlag, interlaceFlag, sortFlag, sizeOfLocalColorTable, sizeOfLocalColorTableInBytes)
    }

    data class ApplicationExtension(val applicationIdentifier: String, val applicationAuthenticationCode: String, val applicationData: ByteArray)
    data class ImageDescriptor(val left: Int, val top: Int, val width: Int, val height: Int, val localColorTableFlag: Boolean, val interlaceFlag: Boolean, val sortFlag: Boolean, val sizeOfLocalColorTable: Int, val sizeOfLocalColorTableInBytes: Int)
    data class GraphicControlExtension(val disposalMethod: Int, val userInputFlag: Boolean, val transparentColorFlag: Boolean, val delayTime: Int, val transparentColorIndex: Int)

    private fun lzw(minCodeSize: Int, input: ByteBuffer, pixelCount: Int, blockSize: Int): ByteArray {
        val MAX_STACK_SIZE = 4096
        val nullCode = -1
        val npix = pixelCount
        var available: Int
        val clear: Int
        var code_mask: Int
        var code_size: Int
        val end_of_information: Int
        var in_code: Int
        var old_code: Int
        var bits: Int
        var code: Int
        var i: Int
        var datum: Int
        val data_size: Int
        var first: Int
        var top: Int
        var bi: Int
        var pi: Int
        val dstPixels = ByteArray(pixelCount)
        val prefix = IntArray(MAX_STACK_SIZE)
        val suffix = IntArray(MAX_STACK_SIZE)
        val pixelStack = IntArray(MAX_STACK_SIZE + 1)

        data_size = minCodeSize
        clear = 1 shl data_size
        end_of_information = clear + 1
        available = clear + 2
        old_code = nullCode
        code_size = data_size + 1
        code_mask = (1 shl code_size) - 1

        for (code in 0 until clear) {
            prefix[code] = 0
            suffix[code] = code
        }

        datum = 0
        bits = 0
        first = 0
        top = 0
        pi = 0
        bi = 0
        i = 0
        var index = 0
        while (i < npix) {
            if (top == 0) {
                if (bits < code_size) {
                    var data = input.get().toInt() and 0xff
                    if(index == blockSize) {
                       index = 0
                       data = input.get().toInt() and 0xff
                    }
                    index++
                    datum += data shl bits
                    bits += 8
                    bi++
                    continue
                }

                code = datum and code_mask
                datum = datum shr code_size
                bits -= code_size

                if (code > available || code == end_of_information) {
                    break
                }

                if (code == clear) {
                    code_size = data_size + 1
                    code_mask = (1 shl code_size) - 1
                    available = clear + 2
                    old_code = nullCode
                    continue
                }

                if (old_code == nullCode) {
                    pixelStack[top++] = suffix[code]
                    old_code = code
                    first = code
                    continue
                }

                in_code = code

                if (code == available) {
                    pixelStack[top++] = first
                    code = old_code
                }

                while (code > clear) {
                    pixelStack[top++] = suffix[code]
                    code = prefix[code]
                }

                first = suffix[code] and 0xff
                pixelStack[top++] = first

                if (available < MAX_STACK_SIZE) {
                    prefix[available] = old_code
                    suffix[available] = first
                    available++

                    if (available and code_mask == 0 && available < MAX_STACK_SIZE) {
                        code_size++
                        code_mask += available
                    }
                }

                old_code = in_code
            }

            top--
            dstPixels[pi++] = pixelStack[top].toByte()
            i++
        }

        for (index in pi until npix) {
            dstPixels[index] = 0
        }
        return dstPixels
    }

    fun getBitmap(globalColorTable: ColorTable?, prevBitmap: Bitmap?): Bitmap? {
        if(frame == null) {
            return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        }
        val colorTable = globalColorTable ?: localColorTable ?: return null
        val globalWidth = header.width
        val globalHeight = header.height

        var width = globalWidth
        var height = globalHeight
        imageDescriptor?.let {
            width = it.width
            height = it.height
        }
        val bitmap = prevBitmap ?: Bitmap.createBitmap(globalWidth, globalHeight, Bitmap.Config.ARGB_8888)
        for (y in 0 until globalHeight) {
            for (x in 0 until globalWidth) {
                val relativeX = x - imageDescriptor!!.left
                val relativeY = y - imageDescriptor!!.top
                if (relativeX < 0 || relativeX >= width || relativeY < 0 || relativeY >= height) {
                    continue
                }
                val index = relativeY * width + relativeX
                val pixel = frame!!.getOrNull(index)
                if(pixel === null) {
                    continue
                }
                val colorIndex = frame!![index].toInt() and 0xFF
                if (colorIndex == graphicControlExtension!!.transparentColorIndex) {
                    if(graphicControlExtension?.disposalMethod == 2) {
                        bitmap.setPixel(x, y, Color.TRANSPARENT)
                    }
                    continue
                }
                val color = colorTable?.getColor(colorIndex)
                if(color === null) {
                    continue
                }
                var colorValue = Color.argb(255, color.red, color.green, color.blue)
                if(color.red == 0 && color.green == 0 && color.blue == 0) {
                  colorValue = Color.TRANSPARENT
                }
                bitmap.setPixel(x, y, colorValue)
            }
        }
        return bitmap
    }
}
