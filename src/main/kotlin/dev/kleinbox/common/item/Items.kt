package dev.kleinbox.common.item

import dev.kleinbox.common.RegisteringContainer
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.Item

object Items : RegisteringContainer<Item>(BuiltInRegistries.ITEM) {

    val HEADBAND = register("headband", GroovingTrinket(
        EquipmentSlot.HEAD,
        GroovingTrinket.basePropertiesWithDance("mesmerizer.default", 20 * 11),
        null
    ))
}