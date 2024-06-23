package dev.kleinbox.dancerizer.common.item

import dev.kleinbox.dancerizer.common.Components
import dev.kleinbox.dancerizer.common.ExpressivePlayer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.item.v1.EquipmentSlotProvider
import net.minecraft.ChatFormatting
import net.minecraft.core.Holder
import net.minecraft.core.component.DataComponentType
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.*

class GroovingTrinket(
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

            if (taunt != null)
                list.add(
                    Component.translatable("item.modifiers.dancerizer.taunt").withColor(ChatFormatting.YELLOW.color!!).append(
                        Component.literal(" ").append(Component.translatable("animation.title.$taunt").withColor(ChatFormatting.YELLOW.color!!))
                    ),
                )
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

        fun gatherItemWithDance(player: ExpressivePlayer) = filterItemsWithComponent(player, Components.DANCE)
        fun gatherItemWithTaunt(player: ExpressivePlayer) = filterItemsWithComponent(player, Components.TAUNT)

        private fun filterItemsWithComponent(player: ExpressivePlayer, component: DataComponentType<out Any>): MutableSet<ItemStack> {
            val items: MutableSet<ItemStack> = mutableSetOf()

            player.`dancerizer$inventory`().armor.forEach {
                if (it.components.get(component) != null)
                    items.add(it)
            }
            if (player.`dancerizer$mainHandItem`().components.get(component) != null)
                items.add(player.`dancerizer$mainHandItem`())

            return items
        }
    }
}