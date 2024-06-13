package dev.kleinbox.common.network.payloads

import dev.kleinbox.Dancerizer.MODID
import dev.kleinbox.common.IExpressivePlayer
import dev.kleinbox.common.network.Payloads
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.Context
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.PlayPayloadHandler
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation.fromNamespaceAndPath


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
            (context.player() as IExpressivePlayer).`dancerizer$setLastEmoteTimestamp`(System.currentTimeMillis())
        }
    }
}