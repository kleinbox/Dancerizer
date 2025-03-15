package dev.kleinbox.dancerizer.common.payload

import dev.kleinbox.dancerizer.Dancerizer.MODID
import dev.kleinbox.dancerizer.common.PlayerExtendedData
import dev.kleinbox.dancerizer.common.item.groovy.GroovingTrinket
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.Context
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.PlayPayloadHandler
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation.fromNamespaceAndPath
import net.minecraft.server.level.ServerPlayer


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

        @Suppress("UnstableApiUsage")
        context.player().server.execute {
            val player = context.player() as ServerPlayer
            val data = player.getAttachedOrCreate(PlayerExtendedData.DATA_TYPE)

            if (data.taunting > 1 || data.danceDuration > 0)
                return@execute

            val allTaunts = GroovingTrinket.gatherItemWithTaunt(player.inventory, player.mainHandItem)
            if (allTaunts.isEmpty()) {
                player.displayClientMessage(Component.translatable("info.$MODID.missing_taunt"), true)
                return@execute
            }

            val availableTaunts = allTaunts.filter { !player.cooldowns.isOnCooldown(it.item) }
            if (availableTaunts.isNotEmpty()) {
                val taunt = availableTaunts.random()
                data.setTaunt(player.level(), player, taunt, false)
                player.setAttached(PlayerExtendedData.DATA_TYPE, data)
            }
        }
    }
}