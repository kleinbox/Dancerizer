package dev.kleinbox.dancerizer.client.animation

import dev.kleinbox.dancerizer.Dancerizer.logger
import kotlinx.serialization.json.*
import net.minecraft.client.animation.AnimationChannel.Interpolations
import net.minecraft.client.animation.Keyframe
import net.minecraft.client.model.geom.ModelPart
import org.joml.Vector3f
import java.util.*
import kotlin.math.abs
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

/**
 * Represents an animation which can be applied to a [net.minecraft.client.model.HumanoidModel].
 *
 * @throws IllegalArgumentException Whenever a field is invalid or missing.
 * @throws NumberFormatException Whenever an expected number in the keyframes cannot be parsed.
 */
class HumanoidPoseManipulator(name: String, path: String, data: JsonObject) {

    companion object {
        private const val VERSION = "1.8" // The supported major.minor version
        private const val MOLANG_NOTICE = "(If you are using molang, then go f yourself)"

        private const val MAGIC_NUMBER: Float = 180f * Math.PI.toFloat() / 10f // Converts degree to radian. Don't ask me what the `/ 10f` is for, at least it works

        /**
         * Extracts the animations from a given Bedrock JSON Animations file.
         *
         * @throws IllegalArgumentException Whenever a field is invalid or missing.
         */
        fun extract(path: String, data: JsonObject): Map<String, HumanoidPoseManipulator> {
            // Check version
            val version: String = data["format_version"]?.jsonPrimitive?.content
                ?: throw IllegalArgumentException("'format_version' is invalid")

            if (!version.startsWith(VERSION))
                logger.warn("'${path}' seems to use an untested version, which is '${version}'; Preferred version is $VERSION.X")

            // Bake all animations
            val animations: JsonObject = data["animations"]?.jsonObject
                ?: throw IllegalArgumentException("'${path}' needs an 'animations' field to be considered valid")

            val baked: MutableMap<String, HumanoidPoseManipulator> = mutableMapOf()
            animations.forEach {
                baked[it.key] = HumanoidPoseManipulator(it.key, path, it.value.jsonObject)
            }

            return baked.toMap()
        }

        enum class Loop(val type: String) {
            PLAY_ONCE("null"),
            HOLD_ON_LAST_FRAME("hold_on_last_frame"),
            LOOP("true");

            companion object {
                fun fromJSON(type: String?): Loop {
                    return enumValues<Loop>().find { it.type == type }
                        ?: throw IllegalArgumentException("field 'loop' is invalid")
                }
            }
        }
    }

    // TODO Getters
    private val loop: Loop = Loop.fromJSON(data["loop"]?.jsonPrimitive?.content.toString())
    val length: Float = runCatching { (data["animation_length"] ?: Json.decodeFromString("0")).jsonPrimitive.float }
        .getOrElse { throw IllegalArgumentException("field 'animation_length' is invalid $MOLANG_NOTICE") }

    // TODO Currently not used
    private val delayStart: Float = runCatching { (data["start_delay"] ?: Json.decodeFromString("0")).jsonPrimitive.float }
        .getOrElse { throw IllegalArgumentException("field 'start_delay' is invalid $MOLANG_NOTICE") }
    private val delayLoop: Float =  runCatching { (data["loop_delay"] ?: Json.decodeFromString("0")).jsonPrimitive.float }
        .getOrElse { throw IllegalArgumentException("field 'loop_delay' is invalid $MOLANG_NOTICE") }
    private val animTimeUpdate: Float = runCatching { (data["anim_time_update"] ?: Json.decodeFromString("0")).jsonPrimitive.float }
        .getOrElse { throw IllegalArgumentException("field 'anim_time_update' is invalid $MOLANG_NOTICE") }
    private val blendWeight: Float = runCatching { (data["blend_weight"] ?: Json.decodeFromString("0")).jsonPrimitive.float }
        .getOrElse { throw IllegalArgumentException("field 'blend_weight' is invalid $MOLANG_NOTICE") }
    private val resetPoseAtStart: Boolean = runCatching { (data["override_previous_animation"] ?: Json.decodeFromString("false")).jsonPrimitive.boolean }
        .getOrElse { throw IllegalArgumentException("field 'override_previous_animation' is invalid") }

    private val keyframe: Map<String, Map<String, NavigableMap<Duration, Keyframe>>> = mapOf(
        "head"      to mapOf(
            "rotation" to TreeMap(),
            "position" to TreeMap(),
        ),
        "body"      to mapOf(
            "rotation" to TreeMap(),
            "position" to TreeMap(),
        ),
        "left_arm"  to mapOf(
            "rotation" to TreeMap(),
            "position" to TreeMap(),
        ),
        "right_arm" to mapOf(
            "rotation" to TreeMap(),
            "position" to TreeMap(),
        ),
        "left_leg"  to mapOf(
            "rotation" to TreeMap(),
            "position" to TreeMap(),
        ),
        "right_leg" to mapOf(
            "rotation" to TreeMap(),
            "position" to TreeMap(),
        ),
    )

    init {
        data["bones"]!!.jsonObject.forEach { bone ->
            if (!keyframe.containsKey(bone.key))
                logger.warn("'${name}' from '${path}' contains unused bone '${bone.key}'")
            else
                bone.value.jsonObject.forEach { posOrRot ->
                    if (posOrRot.key.contains("rotation") || posOrRot.key.contains("position"))
                        when (posOrRot.value) {
                            is JsonObject -> { // Multiple keyframes
                                val array = posOrRot.value.jsonObject
                                array.forEach {
                                    val timestamp = it.key.toDouble().seconds
                                    val value = convertRawToBakedValue(it.value, posOrRot.key, bone.key)
                                        ?: Vector3f()

                                    keyframe[bone.key]!![posOrRot.key]!![timestamp] = Keyframe(
                                        timestamp.toDouble(DurationUnit.SECONDS).toFloat(), value, Interpolations.LINEAR
                                    )
                                }
                            }
                            is JsonArray -> { // Single keyframe
                                val value = convertRawToBakedValue(posOrRot.value, posOrRot.key, bone.key)
                                    ?: Vector3f()

                                keyframe[bone.key]!![posOrRot.key]!![0.0.seconds] = Keyframe(
                                    0f, value, Interpolations.LINEAR
                                )
                            }
                            else -> throw IllegalArgumentException("field '${posOrRot.key}' of bone '${bone.key}' is invalid")
                        }
                }
        }
    }

    fun apply(start: Long,
              timestamp: Long,
              head: ModelPart?,
              body: ModelPart?,
              leftArm: ModelPart?,
              rightArm: ModelPart?,
              leftLeg: ModelPart?,
              rightLeg: ModelPart?) {
        mapOf(
            "head" to head, "body" to body, "left_arm" to leftArm, "right_arm" to rightArm, "left_leg" to leftLeg, "right_leg" to rightLeg,
        ).forEach { part ->
            // Calculate current frame
            val positions = keyframe[part.key]!!["position"]!!
            val rotations = keyframe[part.key]!!["rotation"]!!

            var calculatedPos: Vector3f? = null
            var calculatedRot: Vector3f? = null

            if (!positions.isEmpty()) {
                val posFrames = determineClosestKeyframes(start, timestamp, positions)

                calculatedPos = if (posFrames.second != null) {
                    val timePassed = (timestamp - start).milliseconds.toDouble(DurationUnit.SECONDS)
                    val progress = ((timePassed - posFrames.first.timestamp) / (posFrames.second!!.timestamp - posFrames.first.timestamp)).coerceIn(0.0, 1.0).toFloat()

                    PoseModifier.generateInbetweenFrame(posFrames.first.target, posFrames.second!!.target, progress)
                } else
                    posFrames.first.target
            }

            if (!rotations.isEmpty()) {
                val rotFrames = determineClosestKeyframes(start, timestamp, keyframe[part.key]!!["rotation"]!!)
                calculatedRot = if (rotFrames.second != null) {
                    val timePassed = (timestamp - start).milliseconds.toDouble(DurationUnit.SECONDS)
                    val progress = ((timePassed - rotFrames.first.timestamp) / (rotFrames.second!!.timestamp - rotFrames.first.timestamp)).coerceIn(0.0, 1.0).toFloat()

                    PoseModifier.generateInbetweenFrame(rotFrames.first.target, rotFrames.second!!.target, progress)
                } else
                    rotFrames.first.target
            }

            PoseModifier.setBone(part.value, calculatedPos, calculatedRot, null)
        }
    }

    /**
     * Will determine the two related keyframes from the given map by calculating them via the given timestamps.
     *
     * @throws NullPointerException Whenever the given map is empty and could not find at least one Keyframe.
     */
    private fun determineClosestKeyframes(start: Long, timestamp: Long, keyframes: NavigableMap<Duration, Keyframe>): Pair<Keyframe, Keyframe?> {
        val key = abs(timestamp - start).milliseconds.toDouble(DurationUnit.SECONDS).seconds
        if (keyframes.contains(key)) {
            val lower = keyframes[key]!!
            val higher = keyframes.higherKey(key)?.let { keyframes[it] }
            return Pair(lower, higher)
        } else {
            val lower = keyframes.lowerEntry(key)?.value
            val higher = keyframes.higherEntry(key)?.value

            if (lower != null && higher != null)
                return Pair(lower, higher)
            else if (lower != null || higher != null)
                return Pair((lower ?: higher)!!, null)
            else throw NullPointerException("Given map does not contain a keyframe")
        }
    }

    private fun convertRawToBakedValue(value: JsonElement, type: String, bone: String): Vector3f? = when(value) {
        is JsonArray -> when (type) {
            "rotation" -> Vector3f(
                (value[0].jsonPrimitive.float)/ MAGIC_NUMBER,
                (value[if (value.size > 1) 1 else 0].jsonPrimitive.float)/ MAGIC_NUMBER,
                (value[if (value.size > 1) 2 else 0].jsonPrimitive.float)/ MAGIC_NUMBER
            )
            "position" -> ((when (bone) {
                "head" -> PoseModifier.HEAD
                "body" -> PoseModifier.BODY
                "left_arm" -> PoseModifier.LEFT_ARM
                "right_arm" -> PoseModifier.RIGHT_ARM
                "left_leg" -> PoseModifier.LEFT_LEG
                "right_leg" -> PoseModifier.RIGHT_LEG
                else -> Vector3f()
            }).clone() as Vector3f).add(Vector3f(
                value[0].jsonPrimitive.float,
                -value[if (value.size > 1) 1 else 0].jsonPrimitive.float,
                value[if (value.size > 1) 2 else 0].jsonPrimitive.float))
            else -> null
        }
        is JsonPrimitive -> when (type) {
            "rotation" -> Vector3f(
                (value.float)/ MAGIC_NUMBER,
                (value.float)/ MAGIC_NUMBER,
                (value.float)/ MAGIC_NUMBER
            )
            "position" -> ((when (bone) {
                "head" -> PoseModifier.HEAD
                "body" -> PoseModifier.BODY
                "left_arm" -> PoseModifier.LEFT_ARM
                "right_arm" -> PoseModifier.RIGHT_ARM
                "left_leg" -> PoseModifier.LEFT_LEG
                "right_leg" -> PoseModifier.RIGHT_LEG
                else -> Vector3f()
            }).clone() as Vector3f).add(Vector3f(value.float, -value.float, value.float))
            else -> null
        }
        is JsonObject -> null // TODO: Add JsonObject support
    }
}