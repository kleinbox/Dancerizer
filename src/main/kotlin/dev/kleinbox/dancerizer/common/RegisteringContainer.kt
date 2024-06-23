package dev.kleinbox.dancerizer.common

import dev.kleinbox.dancerizer.Dancerizer.MODID
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation.fromNamespaceAndPath

@Suppress("SameParameterValue")
abstract class RegisteringContainer<T: Any>(private val type: Registry<T>) {

    private val registered: MutableList<T> = mutableListOf()

    fun getRegistered()
        = registered.toList()

    protected fun register(id: String, obj: T): T {
        val generated = Registry.register(type, fromNamespaceAndPath(MODID, id), obj)
        registered.add(generated)
        return generated
    }
}