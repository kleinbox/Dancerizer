package dev.kleinbox.dancerizer.common.compat

import dev.doublekekse.confetti.Confetti
import dev.doublekekse.confetti.math.Vec3Dist
import dev.doublekekse.confetti.packet.ExtendedParticlePacket
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.phys.Vec3


object ConfettiEmitterImplementation : ConfettiEmitter {
    override fun particlePop(player: ServerPlayer) {
        ServerPlayNetworking.send(
            player,
            ExtendedParticlePacket(
                Vec3Dist(player.position().add(0.0, 0.5, 0.0), 0.05),
                Vec3Dist(Vec3(0.0, 0.5, 0.0), 0.2),
                50,
                false,
                Confetti.CONFETTI
            )
        )
    }

    override fun particleRain(player: ServerPlayer) {
        ServerPlayNetworking.send(
            player,
            ExtendedParticlePacket(
                Vec3Dist(player.position().add(0.0, 1.0, 0.0), 0.05),
                Vec3Dist(Vec3(0.0, 0.8, 0.0), 0.1),
                1,
                false,
                Confetti.CONFETTI
            )
        )
    }
}