package dev.kleinbox.common.mixin;

import com.mojang.datafixers.util.Pair;
import dev.kleinbox.common.ExpressivePlayer;
import dev.kleinbox.common.SoundEvents;
import dev.kleinbox.common.Statistics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements ExpressivePlayer {
    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow public abstract void awardStat(ResourceLocation resourceLocation);

    @Unique private short dancerizer$tauntCooldown = 0;
    @SuppressWarnings("WrongEntityDataParameterClass")
    @Unique private static final EntityDataAccessor<Integer> DATA_PLAYER_TAUNTING
            = SynchedEntityData.defineId(Player.class, EntityDataSerializers.INT);

    @SuppressWarnings("WrongEntityDataParameterClass")
    @Unique private static final EntityDataAccessor<Integer> DATA_PLAYER_DANCE_DURATION
            = SynchedEntityData.defineId(Player.class, EntityDataSerializers.INT);
    @SuppressWarnings("WrongEntityDataParameterClass")
    @Unique private static final EntityDataAccessor<Long> DATA_PLAYER_DANCE_TIMESTAMP
            = SynchedEntityData.defineId(Player.class, EntityDataSerializers.LONG);

    @SuppressWarnings("WrongEntityDataParameterClass")
    @Unique private static final EntityDataAccessor<String> DATA_PLAYER_POSE_ANIMATION
            = SynchedEntityData.defineId(Player.class, EntityDataSerializers.STRING);

    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    public void dancerizer$addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        compoundTag.putLong("LastEmoteTimestamp", this.entityData.get(DATA_PLAYER_DANCE_TIMESTAMP));
        compoundTag.putInt("Taunting", dancerizer$isTaunting());
        compoundTag.putString("PoseAnimation", this.entityData.get(DATA_PLAYER_POSE_ANIMATION));
        compoundTag.putInt("Dancing",  this.entityData.get(DATA_PLAYER_DANCE_DURATION));
        compoundTag.putShort("TauntCooldown", this.dancerizer$tauntCooldown);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    public void dancerizer$readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        this.entityData.set(DATA_PLAYER_DANCE_TIMESTAMP, compoundTag.getLong("LastDanceTimestamp"));
        this.entityData.set(DATA_PLAYER_TAUNTING, compoundTag.getInt("Taunting"));
        this.entityData.set(DATA_PLAYER_POSE_ANIMATION, compoundTag.getString("PoseAnimation"));
        this.entityData.set(DATA_PLAYER_DANCE_DURATION, compoundTag.getInt("Dancing"));
        this.dancerizer$tauntCooldown = compoundTag.getShort("TauntCooldown");
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    protected void defineSynchedData(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(DATA_PLAYER_TAUNTING, 0);
        builder.define(DATA_PLAYER_POSE_ANIMATION, "");
        builder.define(DATA_PLAYER_DANCE_TIMESTAMP, 0L);
        builder.define(DATA_PLAYER_DANCE_DURATION, 0);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        int taunt = dancerizer$isTaunting();
        if (taunt <= 0) {
            if (this.dancerizer$tauntCooldown >= 1) {
                this.dancerizer$tauntCooldown--;
                if (this.dancerizer$tauntCooldown == 1)
                    this.entityData.set(DATA_PLAYER_POSE_ANIMATION, "");
            }
        } else {
            taunt--;
            this.entityData.set(DATA_PLAYER_TAUNTING, taunt);
        }

        int duration = dancerizer$isDancePlaying();
        if (duration >= 1) {
            duration--;
            dancerizer$setDancePlaying(duration);
            if (duration == 1)
                this.entityData.set(DATA_PLAYER_POSE_ANIMATION, "");
        }
    }

    @Override
    public long dancerizer$getLastEmoteTimestamp() {
        return this.entityData.get(DATA_PLAYER_DANCE_TIMESTAMP);
    }

    @Override
    public void dancerizer$setLastEmoteTimestamp(long lastEmoteTimestamp, @NotNull Pair<String, Integer> dance) {
        this.entityData.set(DATA_PLAYER_DANCE_TIMESTAMP, lastEmoteTimestamp);
        this.entityData.set(DATA_PLAYER_POSE_ANIMATION, dance.getFirst());
        this.dancerizer$setDancePlaying(dance.getSecond());
    }

    @Override
    public void dancerizer$setTaunt(@NotNull String taunt) {
        if (this.dancerizer$tauntCooldown <= 0) {
            this.entityData.set(DATA_PLAYER_TAUNTING, 5);
            this.entityData.set(DATA_PLAYER_POSE_ANIMATION, taunt);
            this.dancerizer$tauntCooldown = 20;

            if (!level().isClientSide()) {
                level().playSound(
                        null,
                        this.getOnPos(),
                        SoundEvents.INSTANCE.getTAUNT_EVENT(),
                        SoundSource.PLAYERS,
                        this.getSoundVolume(),
                        this.getVoicePitch()
                );

                this.awardStat(Statistics.INSTANCE.getTAUNT());
            }
        }
    }

    @Override
    public String dancerizer$getAnimationPose() {
        return this.entityData.get(DATA_PLAYER_POSE_ANIMATION);
    }

    @Override
    public int dancerizer$isTaunting() {
        return this.entityData.get(DATA_PLAYER_TAUNTING);
    }

    @Override
    public void dancerizer$setDancePlaying(int duration) {
        this.entityData.set(DATA_PLAYER_DANCE_DURATION, duration);
    }

    @Override
    public int dancerizer$isDancePlaying() {
        return this.entityData.get(DATA_PLAYER_DANCE_DURATION);
    }
}
