package dev.kleinbox.dancerizer.client.animation

import net.minecraft.client.model.geom.ModelPart
import org.joml.Vector3f

object PoseModifier {

    private fun calculateCompromiseNum(start: Float, destination: Float, progress: Float): Float {
        val step = (destination-start) * progress
        return start + step
    }

    fun generateInbetweenFrame(start: Vector3f, destination: Vector3f, progress: Float): Vector3f {
        return Vector3f(
            calculateCompromiseNum(start.x, destination.x, progress),
            calculateCompromiseNum(start.y, destination.y, progress),
            calculateCompromiseNum(start.z, destination.z, progress)
        )
    }

    fun setBone(part: ModelPart?, pos: Vector3f?, rot: Vector3f?, scale: Vector3f?) {
        if (part == null)
            return

        if (pos != null)
            part.setPos(pos.x, pos.y, pos.z)

        if (rot != null)
            part.setRotation(rot.x, rot.y, rot.z)

        if (scale != null) {
            part.xScale = scale.x()
            part.yScale = scale.y()
            part.zScale = scale.z()
        } else {
            part.xScale = 1f
            part.yScale = 1f
            part.zScale = 1f
        }
    }

    fun reset(head: ModelPart?,
              body: ModelPart?,
              leftArm: ModelPart?,
              rightArm: ModelPart?,
              leftLeg: ModelPart?,
              rightLeg: ModelPart?) {
        if (head != null && (head.x != HEAD.x || head.y != HEAD.y || head.z != HEAD.z))
            setBone(head, HEAD, NULL, null)
        if (body != null && (body.x != BODY.x || body.y != BODY.y || body.z != BODY.z))
            setBone(body, BODY, NULL, null)
        if (leftArm != null && (leftArm.x != LEFT_ARM.x || leftArm.y != LEFT_ARM.y || leftArm.z != LEFT_ARM.z))
            setBone(leftArm, LEFT_ARM, NULL, null)
        if (rightArm != null && (rightArm.x != RIGHT_ARM.x || rightArm.y != RIGHT_ARM.y || rightArm.z != RIGHT_ARM.z))
            setBone(rightArm, RIGHT_ARM, NULL, null)
        if (leftLeg != null && (leftLeg.x != LEFT_LEG.x || leftLeg.y != LEFT_LEG.y || leftLeg.z != LEFT_LEG.z))
            setBone(leftLeg, LEFT_LEG, NULL, null)
        if (rightLeg != null && (rightLeg.x != RIGHT_LEG.x || rightLeg.y != RIGHT_LEG.y || rightLeg.z != RIGHT_LEG.z))
            setBone(rightLeg, RIGHT_LEG, NULL, null)
    }

    val HEAD = Vector3f(0f, 0f, 0f)
    val BODY = Vector3f(0f, 0f, 0f)
    val LEFT_ARM = Vector3f(5f, 2f, 0f)
    val RIGHT_ARM = Vector3f(-5f, 2f, 0f)
    val LEFT_LEG = Vector3f(1.9f, 12f, 0f)
    val RIGHT_LEG = Vector3f(-1.9f, 12f, 0f)
    val NULL = Vector3f(0f, 0f, 0f)
}