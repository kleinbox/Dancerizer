package dev.kleinbox.common.item

import dev.kleinbox.Dancerizer.MODID
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation.fromNamespaceAndPath
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.Item

object Items {
    private val ITEMS: MutableSet<Pair<String, out Item>> = mutableSetOf()

    val headband = (Pair("headband", GroovingTrinket(
        EquipmentSlot.HEAD,
        GroovingTrinket.basePropertiesWithDance("mesmerizer.default", 20 * 11),
        null
    )).also { ITEMS.add(it) }).second

    init {
        ITEMS.forEach {
            Registry.register(BuiltInRegistries.ITEM, fromNamespaceAndPath(MODID, it.first), it.second)
        }
    }
}