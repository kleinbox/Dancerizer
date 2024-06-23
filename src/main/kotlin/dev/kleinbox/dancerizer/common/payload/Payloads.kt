package dev.kleinbox.dancerizer.common.payload

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload

object Payloads {
    abstract class CustomPayload<T: CustomPacketPayload> : CustomPacketPayload {
        abstract val type: CustomPacketPayload.Type<T>
        abstract val codec: StreamCodec<FriendlyByteBuf, T>
        abstract val handler: ServerPlayNetworking.PlayPayloadHandler<T>

        override fun type() = type
    }

    init {
        registerC2S(DanceTimestampPayload)
        registerC2S(TauntPayload)
    }

    private fun <T : CustomPacketPayload> registerC2S(payload: CustomPayload<T>) {
        PayloadTypeRegistry.playC2S().register(payload.type, payload.codec)
        ServerPlayNetworking.registerGlobalReceiver(payload.type, payload.handler)
    }
}
