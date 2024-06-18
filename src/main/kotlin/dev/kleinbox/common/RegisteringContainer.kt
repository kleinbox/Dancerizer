package dev.kleinbox.common

import dev.kleinbox.Dancerizer.MODID
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation.fromNamespaceAndPath

@Suppress("SameParameterValue")
abstract class RegisteringContainer<T: Any>(private val type: Registry<T>) {

    protected fun register(id: String, obj: T): T {
        return Registry.register(type, fromNamespaceAndPath(MODID, id), obj)
    }
}