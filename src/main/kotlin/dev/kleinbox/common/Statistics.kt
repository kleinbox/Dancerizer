package dev.kleinbox.common

import dev.kleinbox.Dancerizer.MODID
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.resources.ResourceLocation.fromNamespaceAndPath
import net.minecraft.stats.StatFormatter
import net.minecraft.stats.Stats

object Statistics : RegisteringContainer<ResourceLocation>(BuiltInRegistries.CUSTOM_STAT) {

    val TAUNT = register("taunt")

    private fun register(name: String): ResourceLocation {
        val registered = Registry.register(BuiltInRegistries.CUSTOM_STAT, name, fromNamespaceAndPath(MODID, name))
        Stats.CUSTOM.get(registered, StatFormatter.DEFAULT)

        return registered
    }
}