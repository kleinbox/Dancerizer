package dev.kleinbox.dancerizer.common.payload

import dev.kleinbox.dancerizer.Dancerizer.MODID
import dev.kleinbox.dancerizer.common.Components
import dev.kleinbox.dancerizer.common.PlayerExtendedData
import dev.kleinbox.dancerizer.common.api.PlayerAnimationCallback
import dev.kleinbox.dancerizer.common.api.PlayerAnimationStatus
import dev.kleinbox.dancerizer.common.item.groovy.GroovingTrinket
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.Context
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.PlayPayloadHandler
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation.fromNamespaceAndPath
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionResult


/**
 * C2S: Indicates that a new dance has been performed.
 */
object DanceTimestampPayload : Payloads.CustomPayload<DanceTimestampPayload>() {
    override val type: CustomPacketPayload.Type<DanceTimestampPayload>
        = CustomPacketPayload.Type(fromNamespaceAndPath(MODID, "dance_timestamp"))
    override val codec: StreamCodec<FriendlyByteBuf, DanceTimestampPayload>
        = StreamCodec.unit(this)

    override val handler: PlayPayloadHandler<DanceTimestampPayload>
        = PlayPayloadHandler { _: DanceTimestampPayload, context: Context ->

        @Suppress("UnstableApiUsage")
        context.server().execute {
            val player = context.player() as ServerPlayer
            val data = player.getAttachedOrCreate(PlayerExtendedData.DATA_TYPE)

            if (data.taunting > 1 || data.danceDuration > 0)
                return@execute

            val allDances = GroovingTrinket.gatherItemWithDance(player.inventory, player.mainHandItem)
            if (allDances.isEmpty()) {
                player.displayClientMessage(Component.translatable("info.$MODID.missing_dance"), true)
                return@execute
            }

            val availableDances = allDances.filter { !player.cooldowns.isOnCooldown(it.item) }
            if (availableDances.isNotEmpty()) {
                val danceItemStack = availableDances.random()
                val dance = danceItemStack.components.get(Components.DANCE)!!

                val result = PlayerAnimationCallback.EVENT.invoker().interact(
                    data,
                    PlayerAnimationStatus(PlayerAnimationStatus.TYPE.DANCING, dance.second, dance.first)
                )

                if (result != InteractionResult.FAIL)
                    data.setLastEmoteTimestamp(
                        context.player().level().gameTime,
                        dance,
                        context.player(),
                        danceItemStack
                    )
                player.setAttached(PlayerExtendedData.DATA_TYPE, data)
            }
        }
    }
}