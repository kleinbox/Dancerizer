package dev.kleinbox.dancerizer.common.mixin;

import com.mojang.datafixers.util.Pair;
import dev.kleinbox.dancerizer.Dancerizer;
import dev.kleinbox.dancerizer.common.ExpressivePlayer;
import dev.kleinbox.dancerizer.common.SoundEvents;
import dev.kleinbox.dancerizer.common.Statistics;
import dev.kleinbox.dancerizer.common.api.PlayerAnimationCallback;
import dev.kleinbox.dancerizer.common.api.PlayerAnimationStatus;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class AnimationsForPlayerMixin extends LivingEntity implements ExpressivePlayer {
    protected AnimationsForPlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow public abstract void awardStat(ResourceLocation resourceLocation);

    @Shadow @Final Inventory inventory;
    @Unique private short dancerizer$tauntCooldown = 0;
    @Unique
    private boolean wasDancingOrTaunting = false;
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
    protected void dancerizer$defineSynchedData(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(DATA_PLAYER_TAUNTING, 0);
        builder.define(DATA_PLAYER_POSE_ANIMATION, "");
        builder.define(DATA_PLAYER_DANCE_TIMESTAMP, 0L);
        builder.define(DATA_PLAYER_DANCE_DURATION, 0);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void dancerizer$tick(CallbackInfo ci) {
        int taunt = dancerizer$isTaunting();
        if (taunt <= 0) {
            if (this.dancerizer$tauntCooldown == Dancerizer.INSTANCE.getConfig().getData().getServer().getTauntCooldown()) {
                PlayerAnimationCallback.EVENT.invoker().interact(
                        this,
                        new PlayerAnimationStatus(
                                PlayerAnimationStatus.TYPE.TAUNTING,
                                0,
                                dancerizer$getAnimationPose()
                        )
                );
                this.entityData.set(DATA_PLAYER_POSE_ANIMATION, "");
            }
            if (this.dancerizer$tauntCooldown >= 1)
                this.dancerizer$tauntCooldown--;
        } else {
            taunt--;
            this.entityData.set(DATA_PLAYER_TAUNTING, taunt);
        }

        int duration = dancerizer$isDancePlaying();
        if (duration >= 1) {
            this.awardStat(Statistics.INSTANCE.getDANCE());
            duration--;
            dancerizer$setDancePlaying(duration);
            if (duration == 0) {
                PlayerAnimationCallback.EVENT.invoker().interact(
                        this,
                        new PlayerAnimationStatus(
                                PlayerAnimationStatus.TYPE.DANCING,
                                0,
                                dancerizer$getAnimationPose()
                        )
                );
                this.entityData.set(DATA_PLAYER_POSE_ANIMATION, "");
            }
        }
    }

    @Override
    public Inventory dancerizer$inventory() {
        return this.inventory;
    }

    @Override
    public ItemStack dancerizer$mainHandItem() {
        return this.getMainHandItem();
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
    public void dancerizer$setTaunt(@NotNull String taunt, boolean force) {
        InteractionResult result = PlayerAnimationCallback.EVENT.invoker().interact(
                this,
                new PlayerAnimationStatus(PlayerAnimationStatus.TYPE.TAUNTING, Dancerizer.INSTANCE.getConfig().getData().getServer().getTauntDuration(), taunt)
        );
        if (result == InteractionResult.FAIL)
            return;

        if (this.dancerizer$tauntCooldown <= 0 || force) {
            this.entityData.set(DATA_PLAYER_TAUNTING, Dancerizer.INSTANCE.getConfig().getData().getServer().getTauntDuration());
            this.entityData.set(DATA_PLAYER_POSE_ANIMATION, taunt);
            this.dancerizer$tauntCooldown = Dancerizer.INSTANCE.getConfig().getData().getServer().getTauntCooldown();

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

    @Override
    public boolean dancerizer$wasDancingOrTaunting() {
        return this.wasDancingOrTaunting;
    }

    @Override
    public void dancerizer$setWasDancingOrTaunting(boolean value) {
        this.wasDancingOrTaunting = value;
    }
}
