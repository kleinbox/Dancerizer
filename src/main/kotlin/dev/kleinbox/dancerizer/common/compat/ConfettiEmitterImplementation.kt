package dev.kleinbox.dancerizer.common.compat

import dev.doublekekse.confetti.Confetti
import dev.doublekekse.confetti.math.Vec3Dist
import dev.doublekekse.confetti.packet.ExtendedParticlePacket
import dev.kleinbox.dancerizer.Dancerizer.logger
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3


object ConfettiEmitterImplementation : ConfettiEmitter {

    init {
        logger.info("Found Confetti-Lib, making use of that")
    }

    override fun particlePop(player: ServerPlayer) {
        val range = 50.0
        val bounds = player.boundingBox.inflate(range, range, range)
        val players = player.level().getEntitiesOfClass(player.javaClass, bounds)

        players.forEach {
            ServerPlayNetworking.send(
                it,
                ExtendedParticlePacket(
                    Vec3Dist(player.position().add(0.0, 0.5, 0.0), 0.05),
                    Vec3Dist(Vec3(0.0, 0.5, 0.0), 0.2),
                    50,
                    false,
                    Confetti.CONFETTI
                )
            )
        }
    }

    override fun particleRain(player: ServerPlayer) {
        val range = 50.0
        val bounds = player.boundingBox.inflate(range, range, range)
        val players = player.level().getEntitiesOfClass(player.javaClass, bounds)

        players.forEach {
            ServerPlayNetworking.send(
                it,
                ExtendedParticlePacket(
                    Vec3Dist(player.position().add(0.0, 1.0, 0.0), 0.05),
                    Vec3Dist(Vec3(0.0, 0.6, 0.0), 0.8),
                    1,
                    false,
                    Confetti.CONFETTI
                )
            )
        }
    }
}