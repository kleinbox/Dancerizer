package dev.kleinbox.dancerizer.common.item.groovy

import dev.kleinbox.dancerizer.common.Components
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.ChatFormatting
import net.minecraft.core.Holder
import net.minecraft.core.component.DataComponentType
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.*

open class GroovingTrinket(
    private val slot: EquipmentSlot, properties: Properties,
    private val equipSound: Holder<SoundEvent>?) : Item(properties), Equipable {

    override fun getEquipmentSlot(): EquipmentSlot = slot

    override fun getEquipSound(): Holder<SoundEvent> = equipSound ?: SoundEvents.ARMOR_EQUIP_GENERIC

    @Environment(EnvType.CLIENT)
    override fun appendHoverText(
        itemStack: ItemStack,
        tooltipContext: TooltipContext,
        list: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        super.appendHoverText(itemStack, tooltipContext, list, tooltipFlag)

        val taunt = itemStack.components.get(Components.TAUNT)
            ?.substringBeforeLast('.')?.replace('/', '.')
        val dance = itemStack.components.get(Components.DANCE)?.first
            ?.substringBeforeLast('.')?.replace('/', '.')

        if (taunt != null || dance != null && itemStack.item is Equipable) {
            val slot = (itemStack.item as Equipable).equipmentSlot.getName()

            list.addAll(listOf(
                Component.literal(""),
                Component.translatable("item.modifiers.$slot").withColor(ChatFormatting.GRAY.color!!)
            ))

            if (taunt != null) {
                list.add(
                    Component.literal(" ").append(
                        Component.translatable("item.modifiers.dancerizer.taunt")
                            .withColor(ChatFormatting.YELLOW.color!!).append(
                            Component.literal(" ").append(
                                Component.translatable("animation.title.$taunt")
                                    .withColor(ChatFormatting.YELLOW.color!!)
                            )
                        )
                    ),
                )
                list.add(
                    Component.literal(" ").append(
                        Component.translatable("item.modifiers.dancerizer.taunt.effect")
                            .withColor(ChatFormatting.DARK_GRAY.color!!)
                    )
                )
            }
            if (dance != null)
                list.add(
                    Component.translatable("item.modifiers.dancerizer.dance").withColor(ChatFormatting.YELLOW.color!!).append(
                        Component.literal(" ").append(Component.translatable("animation.title.$dance").withColor(ChatFormatting.YELLOW.color!!))
                    ),
                )
        }
    }

    @Suppress("unused")
    companion object {
        fun basePropertiesWithTaunt(taunt: String): Properties = Properties()
            .component(Components.TAUNT, taunt)
            .rarity(Rarity.UNCOMMON)
            .stacksTo(1)
        fun basePropertiesWithDance(dance: String, duration: Int): Properties = Properties()
            .component(Components.DANCE, com.mojang.datafixers.util.Pair(dance, duration))
            .rarity(Rarity.UNCOMMON)
            .stacksTo(1)

        fun gatherItemWithDance(inventory: Inventory, mainHand: ItemStack) = filterItemsWithComponent(inventory, mainHand, Components.DANCE)
        fun gatherItemWithTaunt(inventory: Inventory, mainHand: ItemStack) = filterItemsWithComponent(inventory, mainHand, Components.TAUNT)

        fun hasSpecificAnimation(key: String, inventory: Inventory, mainHand: ItemStack): Boolean {
            val items = gatherItemWithTaunt(inventory, mainHand)
            items.addAll(gatherItemWithDance(inventory, mainHand))

            val filtered = items.filter {
                val dance = it.components.get(Components.DANCE)
                val taunt = it.components.get(Components.TAUNT)

                (dance != null && dance.first == key) || (taunt != null && taunt == key)
            }

            return filtered.isNotEmpty()
        }

        private fun filterItemsWithComponent(inventory: Inventory, mainHand: ItemStack, component: DataComponentType<out Any>): MutableSet<ItemStack> {
            val items: MutableSet<ItemStack> = mutableSetOf()

            inventory.armor.forEach {
                if (it.components.get(component) != null)
                    items.add(it)
            }
            if (mainHand.components.get(component) != null)
                items.add(mainHand)

            return items
        }
    }
}