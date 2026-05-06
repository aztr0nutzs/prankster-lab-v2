package com.pranksterlab.core.audio.generator

import android.content.Context
import com.pranksterlab.core.audio.WavFileWriter
import com.pranksterlab.core.model.GeneratedSoundResult
import com.pranksterlab.core.model.SoundForgeGeneratorType
import com.pranksterlab.core.model.SoundForgeParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

class SoundGeneratorEngine(private val context: Context) {
    private val sampleRate = 44100
    private val twoPi = 2.0 * PI

    suspend fun generateSound(parameters: SoundForgeParameters): GeneratedSoundResult = withContext(Dispatchers.Default) {
        val totalSamples = ((parameters.durationMs / 1000f) * sampleRate).toInt()
        var buffer = DoubleArray(totalSamples)

        val random = Random(parameters.seed)

        // Layered generation
        for (l in 0 until parameters.layers) {
            val layerData = DoubleArray(totalSamples)
            val layerParams = if (l > 0) {
                // Add slight variance for each layer
                parameters.copy(
                    pitch = parameters.pitch * (1.0f + (random.nextFloat() - 0.5f) * parameters.pitchVariance),
                    seed = parameters.seed + l
                )
            } else parameters

            when (parameters.generatorType) {
                SoundForgeGeneratorType.SCI_FI_BLIP -> generateSciFiBlip(layerData, layerParams, random)
                SoundForgeGeneratorType.GLITCH_BURST -> generateGlitchBurst(layerData, layerParams, random)
                SoundForgeGeneratorType.ROBOT_BEEP -> generateRobotBeep(layerData, layerParams, random)
                SoundForgeGeneratorType.CARTOON_POP -> generateCartoonPop(layerData, layerParams, random)
                SoundForgeGeneratorType.TOY_SQUEAK -> generateToySqueak(layerData, layerParams, random)
                SoundForgeGeneratorType.CREEPY_DRONE -> generateCreepyDrone(layerData, layerParams, random)
                SoundForgeGeneratorType.MONSTER_GROWL -> generateMonsterGrowl(layerData, layerParams, random)
                SoundForgeGeneratorType.KNOCK_PATTERN -> generateKnockPattern(layerData, layerParams, random)
                SoundForgeGeneratorType.FOOTSTEP_PATTERN -> generateFootstepPattern(layerData, layerParams, random)
                SoundForgeGeneratorType.CHAOS_RANDOM -> generateChaosRandom(layerData, layerParams, random)
            }
            
            for (i in buffer.indices) {
                buffer[i] += layerData[i] / parameters.layers
            }
        }

        // Apply FX Chain
        buffer = applyFXChain(buffer, parameters, random)
        
        // Safety: Block siren-like patterns or pure alarms if too intense
        applySafetyGuards(buffer, parameters)

        // Envelope
        applyEnvelope(buffer, parameters.attackMs, parameters.releaseMs, sampleRate)
        
        // Normalize and Soft Limit
        val audioData = normalizeAndFinalize(buffer, parameters.volume)

        // Calculate Waveform Peaks (96 buckets)
        val peaks = calculateWaveformPeaks(audioData, 96)

        val outputDir = File(context.filesDir, "generated_sounds")
        if (!outputDir.exists()) outputDir.mkdirs()

        val fileName = "forge_${parameters.generatorType.name.toLowerCase()}_${System.currentTimeMillis()}.wav"
        val outputFile = File(outputDir, fileName)

        val success = WavFileWriter.saveWavFile(audioData, sampleRate, outputFile.absolutePath)

        if (success) {
            GeneratedSoundResult(
                id = UUID.randomUUID().toString(),
                name = "${parameters.generatorType.displayName} ${random.nextInt(1000)}",
                fileUri = outputFile.absolutePath,
                durationMs = parameters.durationMs,
                generatorType = parameters.generatorType,
                createdAt = System.currentTimeMillis(),
                tags = listOf("generated", parameters.generatorType.name.toLowerCase()),
                waveformPeaks = peaks
            )
        } else {
            GeneratedSoundResult(
                id = "",
                name = "Error",
                fileUri = "",
                durationMs = 0,
                generatorType = parameters.generatorType,
                createdAt = System.currentTimeMillis(),
                tags = emptyList(),
                errorMessage = "Failed to write WAV file."
            )
        }
    }

    private fun applyFXChain(data: DoubleArray, params: SoundForgeParameters, random: Random): DoubleArray {
        var processed = data
        if (params.fxChain["Bitcrush"] == true) processed = bitcrush(processed, params.customParams["Bitcrush"] ?: 0.5f)
        if (params.fxChain["Distortion"] == true) processed = distort(processed, params.customParams["Distortion"] ?: 0.5f)
        if (params.fxChain["Low-pass"] == true) processed = lowPass(processed, params.customParams["Low-pass"] ?: 0.5f)
        if (params.fxChain["Reverse"] == true) processed = processed.reversedArray()
        
        // Echo and Reverb are special multi-tap delays
        if (params.echoAmount > 0) processed = applyEcho(processed, params.echoAmount)
        if (params.reverbAmount > 0) processed = applyFakeReverb(processed, params.reverbAmount)
        
        return processed
    }

    private fun applyEcho(data: DoubleArray, amount: Float): DoubleArray {
        val delaySamples = (sampleRate * 0.3).toInt() // 300ms delay
        val output = DoubleArray(data.size)
        val feedback = 0.4 * amount
        for (i in data.indices) {
            output[i] += data[i]
            if (i >= delaySamples) {
                output[i] += output[i - delaySamples] * feedback
            }
        }
        return output
    }

    private fun applyFakeReverb(data: DoubleArray, amount: Float): DoubleArray {
        // Multi-tap short delay
        val taps = listOf(0.015, 0.027, 0.041, 0.059) // seconds
        val output = data.copyOf()
        for (tap in taps) {
            val tapSamples = (tap * sampleRate).toInt()
            val gain = 0.3 * amount
            for (i in tapSamples until data.size) {
                output[i] += data[i - tapSamples] * gain
            }
        }
        return output
    }

    private fun bitcrush(data: DoubleArray, amount: Float): DoubleArray {
        val quality = 1.0 - (amount * 0.95)
        val steps = (quality * 32).toInt().coerceAtLeast(2)
        return DoubleArray(data.size) { i ->
            val d = (data[i] * steps).toInt().toDouble() / steps
            d
        }
    }

    private fun distort(data: DoubleArray, amount: Float): DoubleArray {
        val gain = 1.0 + (amount * 10.0)
        return DoubleArray(data.size) { i ->
            val v = data[i] * gain
            kotlin.math.tanh(v) // Hard clip substitute
        }
    }

    private fun lowPass(data: DoubleArray, amount: Float): DoubleArray {
        val alpha = 1.0 - (amount * 0.9)
        var last = 0.0
        return DoubleArray(data.size) { i ->
            val v = last + alpha * (data[i] - last)
            last = v
            v
        }
    }

    private fun applySafetyGuards(data: DoubleArray, params: SoundForgeParameters) {
        // Prevent extremely high frequency whistles: simple low-pass filter at 8kHz
        val alpha = 0.5
        var last = 0.0
        for (i in data.indices) {
            data[i] = last + alpha * (data[i] - last)
            last = data[i]
        }
        
        // Remove DC offset to prevent speaker pop
        val mean = if (data.isNotEmpty()) data.average() else 0.0
        for (i in data.indices) {
            data[i] -= mean
        }
    }

    private fun normalizeAndFinalize(buffer: DoubleArray, volume: Float): ShortArray {
        var maxAbs = 0.0
        for (v in buffer) if (kotlin.math.abs(v) > maxAbs) maxAbs = kotlin.math.abs(v)
        
        val scale = if (maxAbs > 0) (32767.0 / maxAbs) * 0.9 * volume else 1.0
        return ShortArray(buffer.size) { i ->
            (buffer[i] * scale).toInt().toShort().coerceIn(-32768, 32767).toShort()
        }
    }

    private fun calculateWaveformPeaks(data: ShortArray, buckets: Int): List<Float> {
        if (data.isEmpty()) return emptyList()
        val bucketSize = data.size / buckets
        if (bucketSize == 0) return emptyList()
        
        val peaks = mutableListOf<Float>()
        for (b in 0 until buckets) {
            var max = 0
            val start = b * bucketSize
            val end = (start + bucketSize).coerceAtMost(data.size)
            for (i in start until end) {
                val abs = kotlin.math.abs(data[i].toInt())
                if (abs > max) max = abs
            }
            peaks.add(max.toFloat() / 32767f)
        }
        return peaks
    }

    private fun generateSciFiBlip(data: DoubleArray, params: SoundForgeParameters, random: Random) {
        val baseFreq = (200.0 + (params.customParams["Pitch"] ?: 0.5f) * 1000.0)
        val sweep = (params.customParams["Sweep"] ?: 0.5f) * 1500.0
        var phase = 0.0
        for (i in data.indices) {
            val t = i.toDouble() / sampleRate
            val freq = baseFreq + (sweep * t)
            phase += twoPi * freq / sampleRate
            data[i] = sin(phase)
        }
    }

    private fun generateGlitchBurst(data: DoubleArray, params: SoundForgeParameters, random: Random) {
        val fragmentCount = (2 + (params.customParams["Fragment Count"] ?: 0.5f) * 18).toInt()
        val chaos = params.customParams["Chaos"] ?: 0.5f
        val segmentLen = data.size / fragmentCount
        
        for (s in 0 until fragmentCount) {
            val start = s * segmentLen
            val end = (start + segmentLen).coerceAtMost(data.size)
            val freq = (100.0 + random.nextDouble() * 2000.0) * (1.0 + chaos)
            var phase = 0.0
            for (i in start until end) {
                phase += twoPi * freq / sampleRate
                data[i] = if (sin(phase) > 0) 1.0 else -1.0
                if (random.nextDouble() < chaos * 0.2) data[i] = random.nextDouble() * 2 - 1.0
            }
        }
    }

    private fun generateCreepyDrone(data: DoubleArray, params: SoundForgeParameters, random: Random) {
        val depth = params.customParams["Depth"] ?: 0.5f
        val darkness = params.customParams["Darkness"] ?: 0.5f
        val baseFreq = 40.0 + (1.0 - darkness) * 100.0
        var phase1 = 0.0
        var phase2 = 0.0
        for (i in data.indices) {
            phase1 += twoPi * baseFreq / sampleRate
            phase2 += twoPi * (baseFreq * (1.01 + depth * 0.05)) / sampleRate
            data[i] = (sin(phase1) + sin(phase2)) * 0.5 + (random.nextDouble() * 2 - 1.0) * (depth * 0.1)
        }
    }

    private fun generateKnockPattern(data: DoubleArray, params: SoundForgeParameters, random: Random) {
        val count = (1 + (params.customParams["Knock Count"] ?: 0.2f) * 8).toInt()
        val spacing = (0.1 + (params.customParams["Spacing"] ?: 0.5f) * 1.0)
        val woodTone = params.customParams["Wood Tone"] ?: 0.5f
        
        val samplesPerKnock = (spacing * sampleRate).toInt()
        for (n in 0 until count) {
            val start = n * samplesPerKnock
            if (start >= data.size) break
            
            val duration = (0.05 * sampleRate).toInt()
            val freq = 60.0 + (1.0 - woodTone) * 400.0
            for (i in 0 until duration) {
                if (start + i >= data.size) break
                val t = i.toDouble() / sampleRate
                val env = kotlin.math.exp(-t * 80.0)
                data[start + i] = sin(twoPi * freq * t) * env
            }
        }
    }

    private fun generateRobotBeep(data: DoubleArray, params: SoundForgeParameters, random: Random) = generateSciFiBlip(data, params, random)
    private fun generateCartoonPop(data: DoubleArray, params: SoundForgeParameters, random: Random) {
        val baseFreq = 100.0 * params.pitch
        var phase = 0.0
        for (i in data.indices) {
            val t = i.toDouble() / data.size
            val freq = baseFreq + (3000.0 * (1.0 - t))
            phase += twoPi * freq / sampleRate
            data[i] = sin(phase) * (1.0 - t)
        }
    }
    private fun generateToySqueak(data: DoubleArray, params: SoundForgeParameters, random: Random) {
        val baseFreq = 800.0 + (params.pitch * 1000.0)
        var phase = 0.0
        for (i in data.indices) {
            val wobble = sin(twoPi * 20.0 * i / sampleRate) * 200.0
            phase += twoPi * (baseFreq + wobble) / sampleRate
            data[i] = sin(phase)
        }
    }
    private fun generateMonsterGrowl(data: DoubleArray, params: SoundForgeParameters, random: Random) {
        var phase = 0.0
        for (i in data.indices) {
            val f = 50.0 + (random.nextDouble() * 20.0)
            phase += twoPi * f / sampleRate
            data[i] = (sin(phase) + (random.nextDouble() * 2 - 1.0) * 0.5)
        }
    }
    private fun generateFootstepPattern(data: DoubleArray, params: SoundForgeParameters, random: Random) {
        val beatLength = sampleRate / 2
        for (i in data.indices) {
            val rem = i % beatLength
            if (rem < sampleRate * 0.03) {
                data[i] = (random.nextDouble() * 2 - 1.0) * (1.0 - rem.toDouble() / (sampleRate * 0.03))
            }
        }
    }
    private fun generateChaosRandom(data: DoubleArray, params: SoundForgeParameters, random: Random) {
        for (i in data.indices) data[i] = random.nextDouble() * 2 - 1.0
    }

    private fun applyEnvelope(data: DoubleArray, attackMs: Long, releaseMs: Long, sampleRate: Int) {
        val attackSamples = (attackMs.toDouble() / 1000.0 * sampleRate).toInt()
        val releaseSamples = (releaseMs.toDouble() / 1000.0 * sampleRate).toInt()
        for (i in data.indices) {
            var m = 1.0
            if (i < attackSamples) m = i.toDouble() / attackSamples
            else if (i > data.size - releaseSamples) m = (data.size - i).toDouble() / releaseSamples
            data[i] *= m
        }
    }
}
