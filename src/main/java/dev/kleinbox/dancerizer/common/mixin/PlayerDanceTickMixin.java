package dev.kleinbox.dancerizer.common.mixin;

import dev.kleinbox.dancerizer.common.PlayerExtendedData;
import dev.kleinbox.dancerizer.common.Statistics;
import dev.kleinbox.dancerizer.common.api.v1.PlayerAnimationCallback;
import dev.kleinbox.dancerizer.common.api.v1.PlayerAnimationStatus;
import dev.kleinbox.dancerizer.common.item.groovy.GroovingTrinket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerDanceTickMixin extends LivingEntity {

    protected PlayerDanceTickMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow public abstract void awardStat(ResourceLocation resourceLocation);

    @Shadow @Final Inventory inventory;

    @Inject(method = "tick", at = @At("HEAD"))
    public void dancerizer$tick(CallbackInfo ci) {
        //noinspection UnstableApiUsage
        PlayerExtendedData data = getAttachedOrCreate(PlayerExtendedData.Companion.getDATA_TYPE());

        String pose = data.getPose();
        boolean valid = GroovingTrinket.Companion.hasSpecificAnimation(pose, inventory, getMainHandItem());

        boolean changed = false;

        if (!pose.isEmpty()) {
            if (!valid) {
                data.reset();
                return;
            }

            if (data.getDanceDuration() < 0) {
                data.setDanceStartTimestamp(level().getGameTime());
                changed = true;
            }
        }

        LivingEntity entity = this;
        //noinspection ConstantValue    Don't listen to him
        if (!(entity instanceof Player player))
            return;

        int taunt = data.getTaunting();
        if (taunt > 0) {
            taunt--;
            if (taunt == 1) {
                PlayerAnimationCallback.EVENT.invoker().interact(
                        player,
                        data,
                        new PlayerAnimationStatus(
                                PlayerAnimationStatus.TYPE.TAUNTING,
                                0,
                                pose
                        )
                );
                data.setPose("");
            }

            data.setTaunting(taunt);
            changed = true;
        }

        int duration = data.getDanceDuration();
        if (duration >= 1) {
            this.awardStat(Statistics.INSTANCE.getDANCE());

            duration--;
            data.setDanceDuration(duration);
            changed = true;

            if (duration == 0) {
                PlayerAnimationCallback.EVENT.invoker().interact(
                        player,
                        data,
                        new PlayerAnimationStatus(
                                PlayerAnimationStatus.TYPE.DANCING,
                                0,
                                pose
                        )
                );
                data.setPose("");
                changed = true;
            }
        }

        if (changed)
            //noinspection UnstableApiUsage
            setAttached(PlayerExtendedData.Companion.getDATA_TYPE(), data);
    }
}
