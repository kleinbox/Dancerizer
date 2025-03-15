package dev.kleinbox.dancerizer.common.item.groovy

import net.minecraft.core.Holder
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.entity.EquipmentSlot

class ItemWithTaunt(slot: EquipmentSlot, properties: Properties, equipSound: Holder<SoundEvent>?)
     : GroovingTrinket(slot, properties, equipSound) {
}