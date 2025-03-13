package dev.kleinbox.dancerizer.client.mixin;

import dev.kleinbox.dancerizer.Dancerizer;
import dev.kleinbox.dancerizer.client.animation.Animations;
import dev.kleinbox.dancerizer.client.animation.HumanoidPoseManipulator;
import dev.kleinbox.dancerizer.client.animation.PoseModifier;
import dev.kleinbox.dancerizer.common.ExpressivePlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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
    public void dancerizer$setupAnim(T livingEntity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (livingEntity instanceof ExpressivePlayer player) {
            // Check for valid playing animation
            String anim_type = player.dancerizer$getAnimationPose();

            ModelPart cloak = null;
            if ((Object) this instanceof PlayerModel<?> actualPlayerModel)
                cloak = actualPlayerModel.cloak;

            if (!anim_type.isBlank() && Animations.INSTANCE.getPoses().containsKey(anim_type)) {
                HumanoidPoseManipulator animation = Animations.INSTANCE.getPoses().get(anim_type);

                if (player.dancerizer$isTaunting() > 0) {
                    // Is taunting
                    animation.apply(0, head, body, leftArm, rightArm, leftLeg, rightLeg);
                    hat.copyFrom(head);
                    setCloak(cloak, player);

                    ci.cancel();
                } else if (player.dancerizer$isDancePlaying() > 0) {
                    long timestamp = player.dancerizer$getLastEmoteTimestamp();
                    float deltaTick = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false);
                    long time = livingEntity.level().getGameTime();
                    float elapsedTime = (time - timestamp + deltaTick) / 20F;

                    System.out.println("elapsedTime: "+elapsedTime+", length: "+animation.getLength());

                    if (elapsedTime <= animation.getLength()) {
                        // is dancing
                        animation.apply(elapsedTime, head, body, leftArm, rightArm, leftLeg, rightLeg);
                        hat.copyFrom(head);
                        setCloak(cloak, player);

                        ci.cancel();
                    }
                } else {
                    PoseModifier.INSTANCE.reset(head, body, leftArm, rightArm, leftLeg, rightLeg);
                    hat.copyFrom(head);
                    resetCloak(cloak, player);
                }
            } else {
                PoseModifier.INSTANCE.reset(head, body, leftArm, rightArm, leftLeg, rightLeg);
                hat.copyFrom(head);
                resetCloak(cloak, player);
            }
        }
    }

    @Unique
    private void setCloak(@Nullable ModelPart cloak, ExpressivePlayer player) {
        if (cloak == null)
            return;

        if (!Dancerizer.INSTANCE.getConfig().getData().getClient().getCapeDuringAnimations()) {
            cloak.visible = false;
            return;
        }

        cloak.xRot = -body.xRot;
        cloak.yRot = body.yRot;
        cloak.zRot = -body.zRot;
        cloak.x = -body.x;
        cloak.y = body.y;
        cloak.z = -body.z;
    }

    @Unique
    private void resetCloak(@Nullable ModelPart cloak, ExpressivePlayer player) {
        if (cloak == null)
            return;

        if (!Dancerizer.INSTANCE.getConfig().getData().getClient().getCapeDuringAnimations()) {
            cloak.visible = true;
            return;
        }

        cloak.xRot = 0;
        cloak.yRot = 0;
        cloak.zRot = 0;
        cloak.x = 0;
        cloak.y = 0;
        cloak.z = 0;

    }
}
