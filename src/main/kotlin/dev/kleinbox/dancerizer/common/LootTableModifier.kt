package dev.kleinbox.dancerizer.common

import com.google.common.collect.ImmutableList
import dev.kleinbox.dancerizer.common.item.Items
import net.fabricmc.fabric.api.loot.v3.LootTableEvents
import net.minecraft.world.level.storage.loot.BuiltInLootTables
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue

object LootTableModifier  {
    init {
        LootTableEvents.MODIFY.register(LootTableEvents.Modify { resourceKey, builder, lootTableSource, provider ->
            if (lootTableSource.isBuiltin && resourceKey == BuiltInLootTables.TRAIL_RUINS_ARCHAEOLOGY_COMMON) {
                val pool = LootPool.lootPool()
                    .setBonusRolls(ConstantValue.exactly(0f))
                    .setRolls(ConstantValue.exactly(1f))

                // Add our loot
                Items.getRegistered().forEach {
                    pool.with(LootItem.lootTableItem(it)
                        .setWeight(1)
                        .setQuality(2)
                        .build())
                }

                // Add existing ones
                val pools = builder.pools.build()
                if (pools.size > 0 && pools.first().entries.size > 0) {
                    pools.first().entries.forEach {
                        pool.with(it)
                    }
                }

                // A tiny bit cursed
                builder.pools = ImmutableList.builder()
                builder.pool(pool.build())
            }
        })
    }
}