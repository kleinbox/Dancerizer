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
 * C2S: Indicates that the player is performing a taunt
 */
object TauntPayload : Payloads.CustomPayload<TauntPayload>() {
    override val type: CustomPacketPayload.Type<TauntPayload>
            = CustomPacketPayload.Type(fromNamespaceAndPath(MODID, "taunt"))
    override val codec: StreamCodec<FriendlyByteBuf, TauntPayload>
            = StreamCodec.unit(this)

    override val handler: PlayPayloadHandler<TauntPayload>
            = PlayPayloadHandler { _: TauntPayload, context: Context ->

        context.player().server.execute {
            (context.player() as IExpressivePlayer).`dancerizer$taunt`()
        }
    }
}