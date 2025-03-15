package dev.kleinbox.dancerizer.common

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.kleinbox.dancerizer.Dancerizer.MODID
import dev.kleinbox.dancerizer.Dancerizer.confetti_emitter
import dev.kleinbox.dancerizer.Dancerizer.config
import dev.kleinbox.dancerizer.common.SoundEvents.TAUNT_EVENT
import dev.kleinbox.dancerizer.common.Statistics.TAUNT
import dev.kleinbox.dancerizer.common.api.PlayerAnimationCallback
import dev.kleinbox.dancerizer.common.api.PlayerAnimationStatus
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

@Suppress("UnstableApiUsage")
data class PlayerExtendedData(
    var taunting: Int,
    var danceDuration: Int,
    var danceStartTimestamp: Long,
    var pose: String
) {
    fun reset() {
        taunting = 0
        danceDuration = 0
        danceStartTimestamp = 0
        pose = ""
    }

    fun setLastEmoteTimestamp(lastEmoteTimestamp: Long, dance: Pair<String, Int>, player: Player, danceItemStack: ItemStack) {
        danceDuration = dance.second
        danceStartTimestamp = lastEmoteTimestamp
        pose = dance.first
        player.cooldowns.addCooldown(danceItemStack.item, danceDuration)
    }

    fun setTaunt(level: Level, player: Player, tauntItemStack: ItemStack, force: Boolean) {
        val tauntID = tauntItemStack.components.get(Components.TAUNT)!!

        val result = PlayerAnimationCallback.EVENT.invoker().interact(
            this,
            PlayerAnimationStatus(PlayerAnimationStatus.TYPE.TAUNTING, config.data.server.tauntDuration, tauntID)
        )
        if (result == InteractionResult.FAIL) return

        if (taunting <= 0 || force) {
            taunting = config.data.server.tauntDuration
            pose = tauntID
            player.cooldowns.addCooldown(tauntItemStack.item, config.data.server.tauntCooldown.toInt())

            if (!level.isClientSide()) {
                level.playSound(
                    null,
                    player.onPos,
                    TAUNT_EVENT,
                    SoundSource.PLAYERS,
                    player.soundVolume,
                    player.voicePitch
                )
                confetti_emitter.particlePop(player as ServerPlayer)

                player.awardStat(TAUNT)
            }
        }
    }

    companion object {
        val CODEC: Codec<PlayerExtendedData> = RecordCodecBuilder.create {
            it.group(
                Codec.INT.fieldOf("taunting").forGetter(PlayerExtendedData::taunting),
                Codec.INT.fieldOf("dance_duration").forGetter(PlayerExtendedData::danceDuration),
                Codec.LONG.fieldOf("dance_start_timestamp").forGetter(PlayerExtendedData::danceStartTimestamp),
                Codec.STRING.fieldOf("pose").forGetter(PlayerExtendedData::pose),
            ).apply(it, ::PlayerExtendedData)
        }

        val STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, PlayerExtendedData::taunting,
            ByteBufCodecs.INT, PlayerExtendedData::danceDuration,
            ByteBufCodecs.VAR_LONG, PlayerExtendedData::danceStartTimestamp,
            ByteBufCodecs.STRING_UTF8, PlayerExtendedData::pose,
            ::PlayerExtendedData
        )

        val PLAYER_DATA_ID = ResourceLocation.fromNamespaceAndPath(MODID, "player_extended_data")

        val DATA_TYPE = AttachmentRegistry.create(PLAYER_DATA_ID) { builder ->
            builder.copyOnDeath()
                .initializer { PlayerExtendedData(0, 0, 0, "") }
                .persistent(CODEC)
                .syncWith(STREAM_CODEC, AttachmentSyncPredicate.all())
        }
    }
}
