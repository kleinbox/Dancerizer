package dev.kleinbox.dancerizer.common

import dev.kleinbox.dancerizer.Dancerizer.MODID
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.resources.ResourceLocation.fromNamespaceAndPath
import net.minecraft.stats.StatFormatter
import net.minecraft.stats.Stats

@Suppress("SameParameterValue")
object Statistics : RegisteringContainer<ResourceLocation>(BuiltInRegistries.CUSTOM_STAT) {

    val TAUNT = register("taunt", StatFormatter.DEFAULT)
    val DANCE = register("dance", StatFormatter.TIME)

    private fun register(name: String, formatter: StatFormatter): ResourceLocation {
        val registered = Registry.register(BuiltInRegistries.CUSTOM_STAT, name, fromNamespaceAndPath(MODID, name))
        Stats.CUSTOM.get(registered, formatter)

        return registered
    }
}