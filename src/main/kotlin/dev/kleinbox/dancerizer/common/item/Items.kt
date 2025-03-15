package dev.kleinbox.dancerizer.common.item

import dev.kleinbox.dancerizer.common.RegisteringContainer
import dev.kleinbox.dancerizer.common.item.groovy.GroovingTrinket
import dev.kleinbox.dancerizer.common.item.groovy.ItemWithDance
import dev.kleinbox.dancerizer.common.item.groovy.ItemWithTaunt
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.Item

@Suppress("unused")
object Items : RegisteringContainer<Item>(BuiltInRegistries.ITEM) {

    val MESMERIZING_MASK = register("mesmerizing_mask", ItemWithDance(
        EquipmentSlot.HEAD,
        GroovingTrinket.basePropertiesWithDance("dance/mesmerizer.default", 20 * 11),
        null
    )
    )

    val POKEBOW = register("pokebow", ItemWithDance(
        EquipmentSlot.HEAD,
        GroovingTrinket.basePropertiesWithDance("dance/pokedance.default", (20 * 15.5).toInt()),
        null
    )
    )

    val HORSE_MASK = register("horse_mask", ItemWithDance(
        EquipmentSlot.HEAD,
        GroovingTrinket.basePropertiesWithDance("dance/geddan.default", (20 * 16.4).toInt()),
        null
    )
    )

    val POMPOM = register("pompom", ItemWithDance(
        EquipmentSlot.MAINHAND,
        GroovingTrinket.basePropertiesWithDance("dance/telepathy.default", (20 * 12.04).toInt()).stacksTo(2),
        null
    )
    )

    val MLG_GLASSES = register("mlg_glasses", ItemWithTaunt(
        EquipmentSlot.HEAD,
        GroovingTrinket.basePropertiesWithTaunt("taunt/dab.default"),
        null
    )
    )

    val PLASTIC_ROSE = register("plastic_rose", ItemWithTaunt(
        EquipmentSlot.HEAD,
        GroovingTrinket.basePropertiesWithTaunt("taunt/romanza.default"),
        null
    )
    )

    val ATHLETIC_WINGS = register("athletic_wings", ItemWithTaunt(
        EquipmentSlot.CHEST,
        GroovingTrinket.basePropertiesWithTaunt("taunt/flexing.default"),
        null
    )
    )

    val RRONGS_TIE = register("rrongs_tie", ItemWithTaunt(
        EquipmentSlot.CHEST,
        GroovingTrinket.basePropertiesWithTaunt("taunt/objection.default"),
        null
    )
    )
}