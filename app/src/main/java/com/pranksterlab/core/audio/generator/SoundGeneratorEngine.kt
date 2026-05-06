package com.pranksterlab.core.audio.generator

import android.content.Context
import android.net.Uri
import com.pranksterlab.core.audio.WavFileWriter
import com.pranksterlab.core.model.GeneratedSoundResult
import com.pranksterlab.core.model.SoundForgeGeneratorType
import com.pranksterlab.core.model.SoundForgeParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

class SoundGeneratorEngine(private val context: Context) {
    private val sampleRate = 44100
    private val twoPi = 2.0 * PI

    suspend fun generateSound(parameters: SoundForgeParameters): GeneratedSoundResult = withContext(Dispatchers.Default) {
        val totalSamples = ((parameters.durationMs / 1000f) * sampleRate).toInt().coerceAtLeast(sampleRate / 20)
        var buffer = DoubleArray(totalSamples)

        val random = Random(parameters.seed)
        val derivedLayerLift = ((parameters.customParams["Chaos"] ?: parameters.customParams["Grit"] ?: 0f) * 2f).toInt()
        val layers = (parameters.layers + derivedLayerLift).coerceIn(1, 4)
        val derivedPitchVariance = parameters.pitchVariance +
            ((parameters.customParams["Chaos"] ?: 0f) * 0.08f) +
            ((parameters.customParams["Wobble"] ?: 0f) * 0.05f)

        for (l in 0 until layers) {
            val layerData = DoubleArray(totalSamples)
            val layerParams = if (l > 0) parameters.copy(
                pitch = parameters.pitch * (1.0f + (random.nextFloat() - 0.5f) * derivedPitchVariance),
                seed = parameters.seed + l * 977L
            ) else parameters

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

            for (i in buffer.indices) buffer[i] += layerData[i] / layers
        }

        buffer = applyFXChain(buffer, parameters, random)
        applyDcRemoval(buffer)
        applyEnvelope(buffer, parameters.attackMs, parameters.releaseMs, sampleRate)

        val safetyResult = detectAndMitigateSiren(buffer, parameters.generatorType)
        if (safetyResult != null) {
            return@withContext GeneratedSoundResult(
                id = "",
                name = "Blocked",
                fileUri = Uri.EMPTY,
                durationMs = 0,
                generatorType = parameters.generatorType,
                createdAt = System.currentTimeMillis(),
                tags = emptyList(),
                errorMessage = safetyResult
            )
        }

        val audioData = normalizeAndFinalize(buffer, parameters.volume)
        val peaks = calculateWaveformPeaks(audioData, 96)

        val outputDir = File(context.filesDir, "generated_sounds")
        if (!outputDir.exists()) outputDir.mkdirs()

        val fileName = "forge_${parameters.generatorType.name.lowercase()}_${System.currentTimeMillis()}.wav"
        val outputFile = File(outputDir, fileName)
        val success = WavFileWriter.saveWavFile(audioData, sampleRate, outputFile.absolutePath)

        if (success) {
            GeneratedSoundResult(
                id = UUID.randomUUID().toString(),
                name = "${parameters.generatorType.displayName} ${random.nextInt(1000)}",
                fileUri = Uri.fromFile(outputFile),
                durationMs = parameters.durationMs,
                generatorType = parameters.generatorType,
                createdAt = System.currentTimeMillis(),
                tags = listOf("generated", parameters.generatorType.name.lowercase()),
                waveformPeaks = peaks
            )
        } else {
            GeneratedSoundResult(
                id = "", name = "Error", fileUri = Uri.EMPTY, durationMs = 0,
                generatorType = parameters.generatorType,
                createdAt = System.currentTimeMillis(),
                tags = emptyList(),
                errorMessage = "Failed to write WAV file."
            )
        }
    }

    // ---- FX Chain ---------------------------------------------------------

    private fun applyFXChain(data: DoubleArray, params: SoundForgeParameters, random: Random): DoubleArray {
        var processed = data
        if (params.fxChain["Bitcrush"] == true) processed = bitcrush(processed, params.customParams["Bitcrush"] ?: 0.5f)
        if (params.fxChain["Distortion"] == true) processed = distort(processed, params.customParams["Distortion"] ?: 0.5f)
        if (params.fxChain["Low-pass"] == true) processed = lowPass(processed, params.customParams["Low-pass"] ?: 0.5f)
        if (params.fxChain["Wobble"] == true) processed = wobble(processed, params.customParams["Wobble"] ?: 0.5f)
        if (params.fxChain["Stutter"] == true) processed = stutter(processed, params.customParams["Stutter"] ?: 0.5f)
        if (params.fxChain["Echo"] == true) processed = applyEcho(processed, (params.customParams["Echo"] ?: 0.5f))
        else if ((params.customParams["Echo"] ?: 0f) > 0.01f) processed = applyEcho(processed, params.customParams["Echo"] ?: 0.5f)
        else if (params.echoAmount > 0) processed = applyEcho(processed, params.echoAmount)
        if (params.fxChain["Reverb"] == true) processed = applyFakeReverb(processed, (params.customParams["Reverb"] ?: 0.5f))
        else if (params.reverbAmount > 0) processed = applyFakeReverb(processed, params.reverbAmount)
        if (params.fxChain["Reverse"] == true) processed = processed.reversedArray()
        return processed
    }

    private fun applyEcho(data: DoubleArray, amount: Float): DoubleArray {
        val delaySamples = (sampleRate * 0.28).toInt()
        val output = data.copyOf()
        val feedback = (0.45 * amount).coerceIn(0.0, 0.85)
        for (i in delaySamples until output.size) {
            output[i] += output[i - delaySamples] * feedback
        }
        return output
    }

    private fun applyFakeReverb(data: DoubleArray, amount: Float): DoubleArray {
        val taps = listOf(0.013, 0.029, 0.043, 0.061, 0.089, 0.113)
        val output = data.copyOf()
        val gain = 0.32 * amount
        for (tap in taps) {
            val tapSamples = (tap * sampleRate).toInt()
            for (i in tapSamples until data.size) {
                output[i] += data[i - tapSamples] * gain * (1.0 - tap * 4)
            }
        }
        return output
    }

    private fun bitcrush(data: DoubleArray, amount: Float): DoubleArray {
        val steps = (((1.0 - amount) * 30) + 2).toInt().coerceAtLeast(2)
        val downsample = (1 + amount * 9).toInt().coerceAtLeast(1)
        var hold = 0.0
        return DoubleArray(data.size) { i ->
            if (i % downsample == 0) {
                hold = (data[i] * steps).toInt().toDouble() / steps
            }
            hold
        }
    }

    private fun distort(data: DoubleArray, amount: Float): DoubleArray {
        val gain = 1.0 + (amount * 12.0)
        return DoubleArray(data.size) { i -> kotlin.math.tanh(data[i] * gain) }
    }

    private fun lowPass(data: DoubleArray, amount: Float): DoubleArray {
        val alpha = (1.0 - (amount * 0.92)).coerceIn(0.05, 1.0)
        var last = 0.0
        return DoubleArray(data.size) { i ->
            last += alpha * (data[i] - last)
            last
        }
    }

    private fun wobble(data: DoubleArray, amount: Float): DoubleArray {
        val rateHz = 3.0 + amount * 9.0
        val depth = (0.3 + amount * 0.6).coerceAtMost(0.95)
        return DoubleArray(data.size) { i ->
            val mod = 1.0 - depth * (0.5 - 0.5 * cos(twoPi * rateHz * i / sampleRate))
            data[i] * mod
        }
    }

    private fun stutter(data: DoubleArray, amount: Float): DoubleArray {
        if (data.isEmpty()) return data
        val sliceMs = (140.0 - amount * 110.0).coerceAtLeast(20.0)
        val sliceLen = (sliceMs / 1000.0 * sampleRate).toInt().coerceAtLeast(64)
        val output = DoubleArray(data.size)
        var i = 0
        var rng = Random(0xBEEFL)
        while (i < data.size) {
            val end = (i + sliceLen).coerceAtMost(data.size)
            val repeats = if (rng.nextFloat() < amount * 0.7f) 1 + rng.nextInt(3) else 1
            for (r in 0 until repeats) {
                val baseOffset = i + r * sliceLen
                if (baseOffset >= data.size) break
                for (j in 0 until sliceLen) {
                    if (i + j >= data.size) break
                    val dst = baseOffset + j
                    if (dst >= data.size) break
                    output[dst] = data[i + j]
                }
            }
            i = end + (repeats - 1) * sliceLen
        }
        return output
    }

    // ---- Generators -------------------------------------------------------

    private fun generateSciFiBlip(data: DoubleArray, params: SoundForgeParameters, random: Random) {
        val pitchN = params.customParams["Pitch"] ?: 0.5f
        val sweepN = params.customParams["Sweep"] ?: 0.5f
        val brightN = params.customParams["Brightness"] ?: 0.5f
        val baseFreq = 220.0 + pitchN * 1100.0
        val sweep = (sweepN - 0.5f) * 1800.0
        var phase = 0.0
        for (i in data.indices) {
            val t = i.toDouble() / sampleRate
            val freq = baseFreq + sweep * t
            phase += twoPi * freq / sampleRate
            val s = sin(phase) + brightN * 0.4 * sin(phase * 2)
            data[i] = s
        }
    }

    private fun generateGlitchBurst(data: DoubleArray, params: SoundForgeParameters, random: Random) {
        val fragments = (2 + (params.customParams["Fragment Count"] ?: 0.5f) * 22).toInt()
        val chaos = params.customParams["Chaos"] ?: 0.5f
        val glitch = params.customParams["Glitch"] ?: 0.5f
        val crush = params.customParams["Bitcrush"] ?: 0.5f
        val segLen = (data.size / fragments).coerceAtLeast(64)
        for (s in 0 until fragments) {
            val start = s * segLen
            val end = (start + segLen).coerceAtMost(data.size)
            val freq = (90.0 + random.nextDouble() * 2400.0) * (1.0 + chaos)
            var phase = 0.0
            for (i in start until end) {
                phase += twoPi * freq / sampleRate
                val sq = if (sin(phase) > 0) 1.0 else -1.0
                val noise = if (random.nextFloat() < chaos * 0.25f) random.nextDouble() * 2 - 1.0 else 0.0
                val raw = sq * (1.0 - glitch * 0.4) + noise
                val steps = (2 + (1.0 - crush) * 22.0).toInt().coerceAtLeast(2)
                data[i] = (raw * steps).toInt().toDouble() / steps
            }
        }
    }

    private fun generateRobotBeep(data: DoubleArray, params: SoundForgeParameters, random: Random) {
        val pitchN = params.customParams["Pitch"] ?: 0.5f
        val beeps = (1 + (params.customParams["Beep Count"] ?: 0.4f) * 7).toInt()
        val spacingN = params.customParams["Spacing"] ?: 0.5f
        val robotization = params.customParams["Robotization"] ?: 0.5f
        val freq = 220.0 + pitchN * 900.0
        val period = (data.size / beeps.coerceAtLeast(1)).coerceAtLeast(sampleRate / 8)
        val onLen = (period * (0.25 + spacingN * 0.55)).toInt()
        var phase = 0.0
        for (i in data.indices) {
            val pos = i % period
            phase += twoPi * (freq * (1.0 + 0.04 * sin(twoPi * 11.0 * i / sampleRate))) / sampleRate
            if (pos < onLen) {
                val sq = if (sin(phase) > 0) 1.0 else -1.0
                val tone = (1.0 - robotization) * sin(phase) + robotization * sq
                val env = 1.0 - (pos.toDouble() / onLen)
                data[i] = tone * (0.4 + 0.6 * env)
            }
        }
    }

    private fun generateCartoonPop(data: DoubleArray, params: SoundForgeParameters, random: Random) {
        val pitchN = params.customParams["Pitch"] ?: 0.5f
        val snap = params.customParams["Snap"] ?: 0.6f
        val pops = (1 + (params.customParams["Pop Count"] ?: 0.2f) * 5).toInt()
        val bright = params.customParams["Brightness"] ?: 0.5f
        val perPop = data.size / pops.coerceAtLeast(1)
        for (p in 0 until pops) {
            val start = p * perPop
            val popLen = (perPop * 0.7).toInt()
            val baseFreq = 120.0 + pitchN * 600.0
            var phase = 0.0
            for (i in 0 until popLen) {
                val idx = start + i
                if (idx >= data.size) break
                val t = i.toDouble() / popLen
                val freq = baseFreq + 2400.0 * (1.0 - t) * (1.0 + bright * 0.5)
                phase += twoPi * freq / sampleRate
                val env = exp(-t * (4.0 + snap * 12.0))
                data[idx] += sin(phase) * env
            }
        }
    }

    private fun generateToySqueak(data: DoubleArray, params: SoundForgeParameters, random: Random) {
        val pitchN = params.customParams["Pitch"] ?: 0.5f
        val wobN = params.customParams["Wobble"] ?: 0.5f
        val brightN = params.customParams["Brightness"] ?: 0.5f
        val baseFreq = 700.0 + pitchN * 1500.0
        val wobRate = 4.0 + wobN * 22.0
        val wobDepth = 90.0 + wobN * 320.0
        var phase = 0.0
        for (i in data.indices) {
            val t = i.toDouble() / sampleRate
            val wob = sin(twoPi * wobRate * t) * wobDepth
            phase += twoPi * (baseFreq + wob) / sampleRate
            val s = sin(phase) + brightN * 0.35 * sin(phase * 3)
            data[i] = s
        }
    }

    private fun generateCreepyDrone(data: DoubleArray, params: SoundForgeParameters, random: Random) {
        val depth = params.customParams["Depth"] ?: 0.5f
        val darkness = params.customParams["Darkness"] ?: 0.5f
        val noiseN = params.customParams["Noise"] ?: 0.3f
        val wobN = params.customParams["Wobble"] ?: 0.3f
        val baseFreq = 38.0 + (1.0 - darkness) * 90.0
        var phase1 = 0.0
        var phase2 = 0.0
        for (i in data.indices) {
            val wob = 1.0 + 0.04 * wobN * sin(twoPi * 0.7 * i / sampleRate)
            phase1 += twoPi * baseFreq * wob / sampleRate
            phase2 += twoPi * (baseFreq * (1.012 + depth * 0.05)) / sampleRate
            val noise = (random.nextDouble() * 2 - 1.0) * (noiseN * 0.45)
            data[i] = (sin(phase1) + sin(phase2)) * 0.5 + noise
        }
    }

    private fun generateMonsterGrowl(data: DoubleArray, params: SoundForgeParameters, random: Random) {
        val depth = params.customParams["Depth"] ?: 0.6f
        val grit = params.customParams["Grit"] ?: 0.5f
        val wobN = params.customParams["Wobble"] ?: 0.4f
        val throat = params.customParams["Throat Size"] ?: 0.5f
        val baseFreq = 32.0 + (1.0 - depth) * 70.0
        val formant = 180.0 + throat * 600.0
        var phase = 0.0
        var fphase = 0.0
        for (i in data.indices) {
            val wob = 1.0 + 0.18 * wobN * sin(twoPi * 4.5 * i / sampleRate)
            phase += twoPi * baseFreq * wob / sampleRate
            fphase += twoPi * formant / sampleRate
            val saw = (phase % twoPi) / PI - 1.0
            val grain = (random.nextDouble() * 2 - 1.0) * grit * 0.6
            val core = saw * 0.8 + grain
            val shaped = kotlin.math.tanh(core * (1.0 + grit * 2.0))
            data[i] = shaped * (0.7 + 0.3 * sin(fphase))
        }
    }

    private fun generateKnockPattern(data: DoubleArray, params: SoundForgeParameters, random: Random) {
        val count = (1 + (params.customParams["Knock Count"] ?: 0.3f) * 8).toInt()
        val spacing = (0.12 + (params.customParams["Spacing"] ?: 0.5f) * 0.8)
        val woodTone = params.customParams["Wood Tone"] ?: 0.5f
        val roomSize = params.customParams["Room Size"] ?: 0.3f
        val samplesPerKnock = (spacing * sampleRate).toInt()
        for (n in 0 until count) {
            val start = n * samplesPerKnock
            if (start >= data.size) break
            val duration = (0.05 * sampleRate).toInt()
            val freq = 70.0 + (1.0 - woodTone) * 380.0
            for (i in 0 until duration) {
                if (start + i >= data.size) break
                val t = i.toDouble() / sampleRate
                val env = exp(-t * 75.0)
                data[start + i] += sin(twoPi * freq * t) * env
            }
            // tail (room reflection)
            if (roomSize > 0.05f) {
                val tailLen = (roomSize * 0.35 * sampleRate).toInt()
                for (i in 0 until tailLen) {
                    val idx = start + duration + i
                    if (idx >= data.size) break
                    val t = i.toDouble() / sampleRate
                    val env = exp(-t * (8.0 + 12.0 * (1.0 - roomSize)))
                    data[idx] += (random.nextDouble() * 2 - 1.0) * env * 0.25 * roomSize
                }
            }
        }
    }

    private fun generateFootstepPattern(data: DoubleArray, params: SoundForgeParameters, random: Random) {
        val steps = (2 + (params.customParams["Step Count"] ?: 0.4f) * 8).toInt()
        val spacing = (0.25 + (params.customParams["Spacing"] ?: 0.5f) * 0.6)
        val surface = params.customParams["Surface"] ?: 0.5f
        val heaviness = params.customParams["Heaviness"] ?: 0.5f
        val perStep = (spacing * sampleRate).toInt()
        val stepLen = (0.06 * sampleRate).toInt()
        for (s in 0 until steps) {
            val start = s * perStep
            if (start >= data.size) break
            for (i in 0 until stepLen) {
                if (start + i >= data.size) break
                val t = i.toDouble() / stepLen
                val env = (1.0 - t) * (1.0 - t)
                val noise = random.nextDouble() * 2 - 1.0
                val tone = sin(twoPi * (60.0 + heaviness * 40.0) * i / sampleRate) * 0.5
                data[start + i] += (noise * (0.4 + 0.6 * surface) + tone * heaviness) * env
            }
        }
    }

    private fun generateChaosRandom(data: DoubleArray, params: SoundForgeParameters, random: Random) {
        val density = params.customParams["Density"] ?: 0.7f
        for (i in data.indices) {
            data[i] = if (random.nextFloat() < density) random.nextDouble() * 2 - 1.0 else 0.0
        }
    }

    // ---- Common -----------------------------------------------------------

    private fun applyDcRemoval(data: DoubleArray) {
        if (data.isEmpty()) return
        val mean = data.average()
        for (i in data.indices) data[i] -= mean
    }

    private fun applyEnvelope(data: DoubleArray, attackMs: Long, releaseMs: Long, sampleRate: Int) {
        val attackSamples = (attackMs.toDouble() / 1000.0 * sampleRate).toInt().coerceAtLeast(1)
        val releaseSamples = (releaseMs.toDouble() / 1000.0 * sampleRate).toInt().coerceAtLeast(1)
        for (i in data.indices) {
            var m = 1.0
            if (i < attackSamples) m = i.toDouble() / attackSamples
            else if (i > data.size - releaseSamples) m = (data.size - i).toDouble() / releaseSamples
            data[i] *= m
        }
    }

    private fun normalizeAndFinalize(buffer: DoubleArray, volume: Float): ShortArray {
        var maxAbs = 0.0
        for (v in buffer) if (kotlin.math.abs(v) > maxAbs) maxAbs = kotlin.math.abs(v)
        val scale = if (maxAbs > 0) (32767.0 / maxAbs) * 0.9 * volume else 1.0
        return ShortArray(buffer.size) { i ->
            // soft limiter
            val limited = kotlin.math.tanh(buffer[i] * scale / 32767.0) * 32767.0
            limited.toInt().coerceIn(-32768, 32767).toShort()
        }
    }

    private fun calculateWaveformPeaks(data: ShortArray, buckets: Int): List<Float> {
        if (data.isEmpty()) return emptyList()
        val bucketSize = (data.size / buckets).coerceAtLeast(1)
        val peaks = mutableListOf<Float>()
        for (b in 0 until buckets) {
            var max = 0
            val start = b * bucketSize
            val end = (start + bucketSize).coerceAtMost(data.size)
            for (i in start until end) {
                val a = kotlin.math.abs(data[i].toInt())
                if (a > max) max = a
            }
            peaks.add(max.toFloat() / 32767f)
        }
        return peaks
    }

    /**
     * Simple siren-pattern detection: looks for sustained two-tone alternation in the
     * 400-1500 Hz band with a swing rate of 1-3 Hz, characteristic of emergency sirens.
     * Returns a non-null reason string if the buffer should be blocked.
     */
    private fun detectAndMitigateSiren(data: DoubleArray, type: SoundForgeGeneratorType): String? {
        if (data.size < sampleRate / 2) return null
        // Estimate zero-crossing-rate envelope over 50ms windows
        val win = sampleRate / 20
        val windows = data.size / win
        if (windows < 6) return null
        val zcrs = FloatArray(windows)
        for (w in 0 until windows) {
            var crossings = 0
            for (i in 1 until win) {
                val a = data[w * win + i - 1]
                val b = data[w * win + i]
                if ((a >= 0 && b < 0) || (a < 0 && b >= 0)) crossings++
            }
            zcrs[w] = crossings.toFloat() / win
        }
        // Look at how often ZCR alternates between two distinct levels (suggests two-tone)
        val sorted = zcrs.sortedArray()
        val low = sorted[sorted.size / 4]
        val high = sorted[3 * sorted.size / 4]
        if (high - low < 0.02f) return null
        var alternations = 0
        var prevHigh = zcrs[0] > (low + high) / 2
        for (w in 1 until windows) {
            val isHigh = zcrs[w] > (low + high) / 2
            if (isHigh != prevHigh) alternations++
            prevHigh = isHigh
        }
        val swingRate = alternations.toFloat() / (data.size.toFloat() / sampleRate)
        // 1 Hz to 3 Hz is the classic emergency-siren wail rate.
        if (swingRate in 1.0f..3.5f && (high > 0.04f) && (low > 0.01f)) {
            return "Pattern resembles emergency siren — generation blocked."
        }
        return null
    }
}
