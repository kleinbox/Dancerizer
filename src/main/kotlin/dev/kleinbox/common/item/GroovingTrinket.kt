package dev.kleinbox.common.item

import net.minecraft.core.Holder
import net.minecraft.core.component.DataComponentType
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Equipable
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

class GroovingTrinket(
    private val slot: EquipmentSlot, properties: Properties,
    private val equipSound: Holder<SoundEvent>?) : Item(properties), Equipable {

    override fun getEquipmentSlot(): EquipmentSlot = slot

    override fun getEquipSound(): Holder<SoundEvent> = equipSound ?: SoundEvents.ARMOR_EQUIP_GENERIC

    @Suppress("unused")
    companion object {
        fun basePropertiesWithTaunt(taunt: String): Properties = Properties()
            .component(Components.TAUNT, taunt)

        fun basePropertiesWithDance(dance: String, duration: Int): Properties = Properties()
            .component(Components.DANCE, com.mojang.datafixers.util.Pair(dance, duration))

        fun gatherItemWithDance(player: Player) = filterItemsWithComponent(player, Components.DANCE)
        fun gatherItemWithTaunt(player: Player) = filterItemsWithComponent(player, Components.TAUNT)

        private fun filterItemsWithComponent(player: Player, component: DataComponentType<out Any>): MutableSet<ItemStack> {
            val items: MutableSet<ItemStack> = mutableSetOf()

            player.inventory.armor.forEach {
                if (it.components.get(component) != null)
                    items.add(it)
            }
            if (player.mainHandItem.components.get(component) != null)
                items.add(player.mainHandItem)

            return items
        }
    }
}