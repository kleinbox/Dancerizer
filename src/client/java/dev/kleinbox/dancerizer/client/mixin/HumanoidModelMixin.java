package dev.kleinbox.dancerizer.client.mixin;

import dev.kleinbox.dancerizer.Dancerizer;
import dev.kleinbox.dancerizer.client.animation.Animations;
import dev.kleinbox.dancerizer.client.animation.HumanoidPoseManipulator;
import dev.kleinbox.dancerizer.client.animation.PoseModifier;
import dev.kleinbox.dancerizer.common.ExpressivePlayer;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRenderEvents;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin<T extends LivingEntity> extends AgeableListModel<T> implements ArmedModel, HeadedModel {
    @Shadow @Final public ModelPart head;
    @Shadow @Final public ModelPart hat;
    @Shadow @Final public ModelPart body;
    @Shadow @Final public ModelPart rightArm;
    @Shadow @Final public ModelPart leftArm;
    @Shadow @Final public ModelPart rightLeg;
    @Shadow @Final public ModelPart leftLeg;

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("HEAD"), cancellable = true)
    public void dancerizer$setupAnim(T livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {
        if (livingEntity instanceof ExpressivePlayer player) {
            // Check for valid playing animation
            String anim_type = player.dancerizer$getAnimationPose();

            if (!anim_type.isBlank() && Animations.INSTANCE.getPoses().containsKey(anim_type)) {
                HumanoidPoseManipulator animation = Animations.INSTANCE.getPoses().get(anim_type);

                if (player.dancerizer$isTaunting() > 0) {
                    // Is taunting
                    animation.apply(0, 0, head, body, leftArm, rightArm, leftLeg, rightLeg);
                    hat.copyFrom(head);

                    ci.cancel();
                } else if (player.dancerizer$isDancePlaying() > 0) {
                    long timestamp = player.dancerizer$getLastEmoteTimestamp();
                    long time = System.currentTimeMillis();

                    if ((time - timestamp) <= (animation.getLength() * 1000)) {
                        // is dancing
                        animation.apply(timestamp, time, head, body, leftArm, rightArm, leftLeg, rightLeg);
                        hat.copyFrom(head);

                        ci.cancel();
                    }
                } else {
                    PoseModifier.INSTANCE.reset(head, body, leftArm, rightArm, leftLeg, rightLeg);
                    hat.copyFrom(head);
                }
            } else {
                PoseModifier.INSTANCE.reset(head, body, leftArm, rightArm, leftLeg, rightLeg);
                hat.copyFrom(head);
            }
        }
    }
}
