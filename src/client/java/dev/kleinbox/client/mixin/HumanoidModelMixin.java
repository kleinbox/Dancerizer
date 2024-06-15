package dev.kleinbox.client.mixin;

import dev.kleinbox.Dancerizer;
import dev.kleinbox.client.animation.Animations;
import dev.kleinbox.client.animation.HumanoidPoseManipulator;
import dev.kleinbox.client.animation.PoseModifier;
import dev.kleinbox.common.IExpressivePlayer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public class HumanoidModelMixin<T extends LivingEntity> {
    @Shadow @Final public ModelPart head;
    @Shadow @Final public ModelPart body;
    @Shadow @Final public ModelPart rightArm;
    @Shadow @Final public ModelPart leftArm;
    @Shadow @Final public ModelPart rightLeg;
    @Shadow @Final public ModelPart leftLeg;

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("HEAD"), cancellable = true)
    public void setupAnim(T livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {
        if (livingEntity instanceof IExpressivePlayer player) {
            // TODO We will assume that the point and dab emote have been applied, later, this will check for the item components


            // TODO Reset pose if dance is over
            // Check for playing dance
            if (!Animations.INSTANCE.getPoses().containsKey("mesmerizer.default"))
                Dancerizer.INSTANCE.getLogger().warn("Animation 'mesmerizer.default' has not been found"); // TODO: Don't spam
            else {
                HumanoidPoseManipulator point = Animations.INSTANCE.getPoses().get("mesmerizer.default");

                long timestamp = player.dancerizer$getLastEmoteTimestamp();
                long time = System.currentTimeMillis();

                if ((time - timestamp) <= (point.getLength() * 1000)) {
                    //player.dancerizer$played = true;
                    point.apply(timestamp, time, head, body, leftArm, rightArm, leftLeg, rightLeg);
                    ci.cancel();
                }
            }

            // Check for taunt
            if (player.dancerizer$isTaunting() > 1) {
                if (!Animations.INSTANCE.getPoses().containsKey("dab.default"))
                    Dancerizer.INSTANCE.getLogger().warn("Animation 'dab.default' has not been found");
                else {
                    HumanoidPoseManipulator dab = Animations.INSTANCE.getPoses().get("dab.default");
                    dab.apply(0, 0, head, body, leftArm, rightArm, leftLeg, rightLeg);

                    ci.cancel();
                }
            } else if (player.dancerizer$isTaunting() == 1) {
                PoseModifier.INSTANCE.reset(head, body, leftArm, rightArm, leftLeg, rightLeg);
            }
        }
    }
}
