package com.pranksterlab.core.audio

import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object WavFileWriter {
    fun saveWavFile(audioData: ShortArray, sampleRate: Int, outputPath: String): Boolean {
        val file = File(outputPath)
        try {
            val os = FileOutputStream(file)
            val byteRate = sampleRate * 2 // 16 bit mono
            val dataSize = audioData.size * 2
            val totalDataLen = dataSize + 36

            val header = ByteArray(44)
            // RIFF/WAVE header
            header[0] = 'R'.code.toByte()
            header[1] = 'I'.code.toByte()
            header[2] = 'F'.code.toByte()
            header[3] = 'F'.code.toByte()
            header[4] = (totalDataLen and 0xff).toByte()
            header[5] = (totalDataLen shr 8 and 0xff).toByte()
            header[6] = (totalDataLen shr 16 and 0xff).toByte()
            header[7] = (totalDataLen shr 24 and 0xff).toByte()
            header[8] = 'W'.code.toByte()
            header[9] = 'A'.code.toByte()
            header[10] = 'V'.code.toByte()
            header[11] = 'E'.code.toByte()
            header[12] = 'f'.code.toByte() // fmt ' chunk
            header[13] = 'm'.code.toByte()
            header[14] = 't'.code.toByte()
            header[15] = ' '.code.toByte()
            header[16] = 16 // 4 bytes: size of 'fmt ' chunk
            header[17] = 0
            header[18] = 0
            header[19] = 0
            header[20] = 1 // format = 1
            header[21] = 0
            header[22] = 1 // channels = 1
            header[23] = 0
            header[24] = (sampleRate and 0xff).toByte()
            header[25] = (sampleRate shr 8 and 0xff).toByte()
            header[26] = (sampleRate shr 16 and 0xff).toByte()
            header[27] = (sampleRate shr 24 and 0xff).toByte()
            header[28] = (byteRate and 0xff).toByte()
            header[29] = (byteRate shr 8 and 0xff).toByte()
            header[30] = (byteRate shr 16 and 0xff).toByte()
            header[31] = (byteRate shr 24 and 0xff).toByte()
            header[32] = 2 // block align (channels * byte_per_sample)
            header[33] = 0
            header[34] = 16 // bits per sample
            header[35] = 0
            header[36] = 'd'.code.toByte()
            header[37] = 'a'.code.toByte()
            header[38] = 't'.code.toByte()
            header[39] = 'a'.code.toByte()
            header[40] = (dataSize and 0xff).toByte()
            header[41] = (dataSize shr 8 and 0xff).toByte()
            header[42] = (dataSize shr 16 and 0xff).toByte()
            header[43] = (dataSize shr 24 and 0xff).toByte()

            os.write(header, 0, 44)

            // Write 16-bit PCM audio data
            val audioBytes = ByteArray(audioData.size * 2)
            for (i in audioData.indices) {
                val shortVal = audioData[i].toInt()
                audioBytes[i * 2] = (shortVal and 0xff).toByte()
                audioBytes[i * 2 + 1] = (shortVal shr 8 and 0xff).toByte()
            }
            os.write(audioBytes)
            os.close()
            return true
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
    }
}
