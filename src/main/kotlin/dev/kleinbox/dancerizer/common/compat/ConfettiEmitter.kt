package dev.kleinbox.dancerizer.common.compat

import net.minecraft.server.level.ServerPlayer

interface ConfettiEmitter {

    fun particlePop(player: ServerPlayer)
    fun particleRain(player: ServerPlayer)

    companion object {
        object Dummy : ConfettiEmitter {
            override fun particlePop(player: ServerPlayer) { }

            override fun particleRain(player: ServerPlayer) { }
        }
    }
}