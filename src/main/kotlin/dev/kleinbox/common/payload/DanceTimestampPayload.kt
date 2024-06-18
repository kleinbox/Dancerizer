package dev.kleinbox.common.payload

import dev.kleinbox.Dancerizer.MODID
import dev.kleinbox.common.ExpressivePlayer
import dev.kleinbox.common.Components
import dev.kleinbox.common.api.PlayerAnimationCallback
import dev.kleinbox.common.api.PlayerAnimationStatus
import dev.kleinbox.common.item.GroovingTrinket
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

        context.player().server.execute {
            val player = context.player() as ServerPlayer
            if ((player as ExpressivePlayer).`dancerizer$isTaunting`() > 0)
                return@execute

            val dances = GroovingTrinket.gatherItemWithDance(player)

            if (dances.isNotEmpty()) {
                val dance = dances.random().components.get(Components.DANCE)!!

                val result = PlayerAnimationCallback.EVENT.invoker().interact(
                    player as ExpressivePlayer,
                    PlayerAnimationStatus(PlayerAnimationStatus.TYPE.DANCING, dance.second, dance.first)
                )

                if (result != InteractionResult.FAIL)
                    (player as ExpressivePlayer).`dancerizer$setLastEmoteTimestamp`(System.currentTimeMillis(), dance)
            } else
                player.displayClientMessage(Component.translatable("info.$MODID.missing_dance"), true)
        }
    }
}