package dev.kleinbox.common

import dev.kleinbox.Dancerizer.MODID
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.resources.ResourceLocation.fromNamespaceAndPath
import net.minecraft.sounds.SoundEvent



@Suppress("MemberVisibilityCanBePrivate")
object SoundEvents {
    val TAUNT_ID: ResourceLocation = fromNamespaceAndPath(MODID, "taunt")
    var TAUNT_EVENT: SoundEvent = SoundEvent.createVariableRangeEvent(TAUNT_ID)

    init {
        Registry.register(BuiltInRegistries.SOUND_EVENT, TAUNT_ID, TAUNT_EVENT)
    }
}