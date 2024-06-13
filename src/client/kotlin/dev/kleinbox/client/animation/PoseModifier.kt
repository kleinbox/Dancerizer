package dev.kleinbox.client.animation

import net.minecraft.client.model.geom.ModelPart
import org.joml.Vector3f

object PoseModifier {

    private fun calculateCompromiseNum(start: Float, destination: Float, progress: Float): Float {
        val step = (destination-start) * progress // TODO Add more transition types like easeInOutBounce(progress.toFloat())
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
        setBone(head, HEAD, null, null)
        setBone(body, BODY, null, null)
        setBone(leftArm, LEFT_ARM, null, null)
        setBone(rightArm, RIGHT_ARM, null, null)
        setBone(leftLeg, LEFT_LEG, null, null)
        setBone(rightLeg, RIGHT_LEG, null, null)
    }

    val HEAD = Vector3f(0f, -2f, 0f)
    val BODY = Vector3f(0f, 0f, 0f)
    val LEFT_ARM = Vector3f(5f, 2f, 0f)
    val RIGHT_ARM = Vector3f(-5f, 2f, 0f)
    val LEFT_LEG = Vector3f(1.9f, 12f, 0f)
    val RIGHT_LEG = Vector3f(-1.9f, 12f, 0f)
}