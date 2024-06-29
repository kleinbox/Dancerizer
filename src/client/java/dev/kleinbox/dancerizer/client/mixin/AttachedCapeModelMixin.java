package dev.kleinbox.dancerizer.client.mixin;

import dev.kleinbox.dancerizer.common.ExpressivePlayer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerModel.class)
public abstract class AttachedCapeModelMixin<T extends LivingEntity> extends HumanoidModel<T> {
    @Shadow @Final private ModelPart cloak;

    public AttachedCapeModelMixin(ModelPart modelPart) {
        super(modelPart);
    }

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("TAIL"))
    public void dancerizer$AttachCape(T livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {
        if (!cloak.visible) {
            return;
        }
        if (!(livingEntity instanceof ExpressivePlayer expressivePlayer)) {
            return;
        }
        boolean dancingOrTaunting = (expressivePlayer.dancerizer$isDancePlaying() > 1 || expressivePlayer.dancerizer$isTaunting() > 1);
        if (dancingOrTaunting) {
            // get cloak to align with body
            cloak.xRot = -body.xRot;
            cloak.yRot = body.yRot;
            cloak.zRot = -body.zRot;
            cloak.x = -body.x;
            cloak.z = -body.z;
            expressivePlayer.dancerizer$setWasDancingOrTaunting(true);
        }
        boolean wasDancingOrTaunting = expressivePlayer.dancerizer$wasDancingOrTaunting();
        if (wasDancingOrTaunting && !dancingOrTaunting) {
            // reset cloak after dance finished
            cloak.xRot = 0;
            cloak.yRot = 0;
            cloak.zRot = 0;
            cloak.x = 0;
            cloak.y = 0;
            cloak.z = 0;
            expressivePlayer.dancerizer$setWasDancingOrTaunting(false);
        }
    }
}
