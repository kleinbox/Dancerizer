package dev.kleinbox.common.mixin;

import dev.kleinbox.common.IExpressivePlayer;
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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements IExpressivePlayer {
    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow public abstract void awardStat(ResourceLocation resourceLocation);

    @Unique private short tauntCooldown = 0;
    @SuppressWarnings("WrongEntityDataParameterClass")
    @Unique private static final EntityDataAccessor<Integer> DATA_PLAYER_TAUNTING
            = SynchedEntityData.defineId(Player.class, EntityDataSerializers.INT);
    @SuppressWarnings("WrongEntityDataParameterClass")
    @Unique private static final EntityDataAccessor<Long> DATA_PLAYER_DANCE_TIMESTAMP
            = SynchedEntityData.defineId(Player.class, EntityDataSerializers.LONG);

    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    public void dancerizer$addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        compoundTag.putLong("LastEmoteTimestamp", this.entityData.get(DATA_PLAYER_DANCE_TIMESTAMP));
        compoundTag.putInt("Taunting", dancerizer$isTaunting());
        compoundTag.putShort("TauntCooldown", this.tauntCooldown);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    public void dancerizer$readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        this.entityData.set(DATA_PLAYER_DANCE_TIMESTAMP, compoundTag.getLong("LastEmoteTimestamp"));
        this.entityData.set(DATA_PLAYER_TAUNTING, compoundTag.getInt("Taunting"));
        this.tauntCooldown = compoundTag.getShort("TauntCooldown");
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    protected void defineSynchedData(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(DATA_PLAYER_TAUNTING, 0);
        builder.define(DATA_PLAYER_DANCE_TIMESTAMP, 0L);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        int taunt = dancerizer$isTaunting();
        if (taunt <= 0 && this.tauntCooldown > 0) {
            this.tauntCooldown--;
        } else {
            taunt--;
            this.entityData.set(DATA_PLAYER_TAUNTING, taunt);
        }
    }

    @Override
    public long dancerizer$getLastEmoteTimestamp() {
        return this.entityData.get(DATA_PLAYER_DANCE_TIMESTAMP);
    }

    @Override
    public void dancerizer$setLastEmoteTimestamp(long lastEmoteTimestamp) {
        this.entityData.set(DATA_PLAYER_DANCE_TIMESTAMP, lastEmoteTimestamp);

    }

    @Override
    public void dancerizer$taunt() {
        if (this.tauntCooldown <= 0) {
            this.entityData.set(DATA_PLAYER_TAUNTING, 5);
            this.tauntCooldown = 20;

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
    public int dancerizer$isTaunting() {
        return this.entityData.get(DATA_PLAYER_TAUNTING);
    }
}
