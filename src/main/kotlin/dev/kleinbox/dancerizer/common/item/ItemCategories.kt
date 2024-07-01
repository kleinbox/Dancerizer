package dev.kleinbox.dancerizer.common.item

import dev.kleinbox.dancerizer.Dancerizer.MODID
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation.fromNamespaceAndPath
import net.minecraft.world.item.ItemStack

object ItemCategories {

    private val group = FabricItemGroup.builder()
        .icon { ItemStack(Items.MESMERIZING_MASK) }
        .title(Component.translatable("itemGroup.$MODID"))
        .build()

    init {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, fromNamespaceAndPath(MODID, "item_group"), group)

        val groupRegistryKey = ResourceKey.create(
            BuiltInRegistries.CREATIVE_MODE_TAB.key(),
            fromNamespaceAndPath(MODID, "item_group")
        )

        ItemGroupEvents.modifyEntriesEvent(groupRegistryKey).register { group ->
            Items.getRegistered().reversed().forEach { item -> group.prepend(item) }
        }
    }
}