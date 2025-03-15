package dev.kleinbox.dancerizer.common

import dev.kleinbox.dancerizer.common.PlayerExtendedData.Companion.DATA_TYPE
import dev.kleinbox.dancerizer.common.item.groovy.GroovingTrinket.Companion.gatherItemWithTaunt
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents
import net.minecraft.util.Mth
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

object TauntDamageDenial {
    init {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(::onDamage)
    }

    @Suppress("UnstableApiUsage")
    fun onDamage(entity: LivingEntity, source: DamageSource, amount: Float) : Boolean {
        val data: PlayerExtendedData? = entity.getAttached(DATA_TYPE)

        if (data == null || entity !is Player)
            return true

        if (entity.isCreative || entity.isSpectator)
            return false

        val taunts = gatherItemWithTaunt(entity.inventory, entity.mainHandItem).filter { !entity.cooldowns.isOnCooldown(it.item) }

        val chance = (100f / 6) * taunts.size
        val roll = (Mth.randomBetween(entity.random, 0f, 1f) * 100)

        if (roll <= chance) {
            val itemStack = taunts[Mth.randomBetweenInclusive(entity.random, 0, taunts.size - 1)]

            data.reset()
            data.setTaunt(entity.level(), entity, itemStack, true)
            return false
        }

        return true
    }
}