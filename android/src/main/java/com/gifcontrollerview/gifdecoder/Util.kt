package com.gifcontrollerview.gifdecoder

import android.util.Log
import com.gifcontrollerview.gifdecoder.toByteArray
import java.nio.ByteBuffer

fun recursiveCheckApplicationExtension(byteBuffer: ByteBuffer) {
    while (true) {
        if(!checkApplicationExtension(byteBuffer)){
           break
        }
    }
}

fun checkApplicationExtension (byteBuffer: ByteBuffer): Boolean {
    val nextPosition = byteBuffer.position()
    if (byteBuffer.get(nextPosition).toInt() and 0xff == 0x21
        && byteBuffer.get(nextPosition + 1).toInt() and 0xff == 0xFF
    ) {
        val extensionCode = byteBuffer.get()
        val extensionLabel = byteBuffer.get()
        val blockSize = byteBuffer.get()
        val applicationIdentifier = byteBuffer.toByteArray(blockSize.toInt() and 0xff)
        var subBlockSize = byteBuffer.get()
        while (subBlockSize.toInt() != 0) {
            val applicationAuthenticationCode = byteBuffer.toByteArray(subBlockSize.toInt() and 0xff)
            subBlockSize = byteBuffer.get()
        }
        return true
    }
    return false
}
