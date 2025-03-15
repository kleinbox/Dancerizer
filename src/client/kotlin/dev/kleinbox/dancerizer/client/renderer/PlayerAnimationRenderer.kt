package dev.kleinbox.dancerizer.client.renderer

import dev.kleinbox.dancerizer.Dancerizer.config
import dev.kleinbox.dancerizer.client.animation.Animations.poses
import dev.kleinbox.dancerizer.client.animation.PoseModifier.reset
import dev.kleinbox.dancerizer.common.PlayerExtendedData
import net.minecraft.client.Minecraft
import net.minecraft.client.model.AgeableListModel
import net.minecraft.client.model.PlayerModel
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import org.spongepowered.asm.mixin.Unique
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

object PlayerAnimationRenderer {
    fun setupAnim(
        livingEntity: LivingEntity,
        model: AgeableListModel<LivingEntity>,
        ci: CallbackInfo
    ) {
        if (livingEntity !is Player)
            return
        if (model !is PlayerModel<*>)
            return

        @Suppress("UnstableApiUsage")
        val data = livingEntity.getAttachedOrCreate(PlayerExtendedData.DATA_TYPE)

        val cloak: ModelPart = model.cloak

        if (data.pose.isNotBlank() && poses.containsKey(data.pose)) {
            val animation = poses[data.pose]

            if (data.taunting > 0) {
                // Is taunting
                animation!!.apply(0f, model.head, model.body, model.leftArm, model.rightArm, model.leftLeg, model.rightLeg)
                model.hat.copyFrom(model.head)
                setCloak(cloak, model)

                ci.cancel()
            } else if (data.danceDuration > 0) {
                val timestamp: Long = data.danceStartTimestamp
                val deltaTick = Minecraft.getInstance().timer.getGameTimeDeltaPartialTick(false)
                val time = livingEntity.level().gameTime
                val elapsedTime = (time - timestamp + deltaTick) / 20f

                if (elapsedTime <= animation!!.length) {
                    // is dancing
                    animation.apply(elapsedTime, model.head, model.body, model.leftArm, model.rightArm, model.leftLeg, model.rightLeg)
                    model.hat.copyFrom(model.head)
                    setCloak(cloak, model)

                    ci.cancel()
                }
            } else {
                reset(model.head, model.body, model.leftArm,model. rightArm, model.leftLeg, model.rightLeg)
                model.hat.copyFrom(model.head)
                resetCloak(cloak)
            }
        } else {
            reset(model.head, model.body, model.leftArm, model.rightArm, model.leftLeg, model.rightLeg)
            model.hat.copyFrom(model.head)
            resetCloak(cloak)
        }
    }

    @Unique
    private fun setCloak(cloak: ModelPart?, model: PlayerModel<*>) {
        if (cloak == null) return

        if (!config.data.client.capeDuringAnimations) {
            cloak.visible = false
            return
        }

        cloak.xRot = -model.body.xRot
        cloak.yRot = model.body.yRot
        cloak.zRot = -model.body.zRot
        cloak.x = -model.body.x
        cloak.y = model.body.y
        cloak.z = -model.body.z
    }

    @Unique
    private fun resetCloak(cloak: ModelPart?) {
        if (cloak == null)
            return

        if (!config.data.client.capeDuringAnimations) {
            cloak.visible = true
            return
        }

        cloak.xRot = 0f
        cloak.yRot = 0f
        cloak.zRot = 0f
        cloak.x = 0f
        cloak.y = 0f
        cloak.z = 0f
    }
}