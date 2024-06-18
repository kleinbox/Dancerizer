package dev.kleinbox.common

import dev.kleinbox.Dancerizer.MODID
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation.fromNamespaceAndPath
import net.minecraft.sounds.SoundEvent

@Suppress("MemberVisibilityCanBePrivate")
object SoundEvents : RegisteringContainer<SoundEvent>(BuiltInRegistries.SOUND_EVENT) {

    var TAUNT_EVENT: SoundEvent = register(
        "taunt",
        SoundEvent.createVariableRangeEvent(fromNamespaceAndPath(MODID, "taunt"))
    )
}