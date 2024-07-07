package dev.kleinbox.dancerizer.client.mixin;

import dev.kleinbox.dancerizer.client.animation.Animations;
import dev.kleinbox.dancerizer.client.animation.HumanoidPoseManipulator;
import dev.kleinbox.dancerizer.common.ExpressivePlayer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerModel.class)
public abstract class AttachedCapeModelMixin<T extends LivingEntity> extends HumanoidModel<T> {
    @Shadow
    @Final
    private ModelPart cloak;

    public AttachedCapeModelMixin(ModelPart modelPart) {
        super(modelPart);
    }

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("TAIL"))
    public void dancerizer$AttachCape(T livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {
        if (!cloak.visible)
            return;
        if (!(livingEntity instanceof ExpressivePlayer player))
            return;

        String anim_type = player.dancerizer$getAnimationPose();
        boolean wasDancingOrTaunting = player.dancerizer$wasDancingOrTaunting();

        if (!anim_type.isBlank() && Animations.INSTANCE.getPoses().containsKey(anim_type)) {
            HumanoidPoseManipulator animation = Animations.INSTANCE.getPoses().get(anim_type);

            if (player.dancerizer$isTaunting() > 0) {
                setCloak(player);
            } else if (player.dancerizer$isDancePlaying() > 0) {
                long timestamp = player.dancerizer$getLastEmoteTimestamp();
                long time = System.currentTimeMillis();

                if ((time - timestamp) <= (animation.getLength() * 1000)) {
                    setCloak(player);
                }
            } else if (wasDancingOrTaunting) {
                resetCloak(player);
            }
        } else if (wasDancingOrTaunting) {
            resetCloak(player);
        }
    }

    @Unique
    private void setCloak(ExpressivePlayer player) {
        cloak.xRot = -body.xRot;
        cloak.yRot = body.yRot;
        cloak.zRot = -body.zRot;
        cloak.x = -body.x;
        cloak.y = body.y;
        cloak.z = -body.z;
        player.dancerizer$setWasDancingOrTaunting(true);
    }

    @Unique
    private void resetCloak(ExpressivePlayer player) {
        cloak.xRot = 0;
        cloak.yRot = 0;
        cloak.zRot = 0;
        cloak.x = 0;
        cloak.y = 0;
        cloak.z = 0;
        player.dancerizer$setWasDancingOrTaunting(false);
    }
}
