package dev.kleinbox.common.item

import com.mojang.serialization.Codec
import dev.kleinbox.Dancerizer.MODID
import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.resources.ResourceLocation.fromNamespaceAndPath

object Components {

    private val COMPONENTS: MutableSet<Pair<String, DataComponentType<out Any>>> = mutableSetOf()

    val DANCE: DataComponentType<com.mojang.datafixers.util.Pair<String, Int>> = Pair("dance", DataComponentType.builder<com.mojang.datafixers.util.Pair<String, Int>>()
        .persistent(Codec.pair(Codec.STRING, Codec.INT))
        .networkSynchronized(ByteBufCodecs.fromCodecWithRegistries(Codec.pair(Codec.STRING, Codec.INT)))
        .build()).also { COMPONENTS.add(it) }.second

    val TAUNT: DataComponentType<String> = Pair("taunt", DataComponentType.builder<String>()
        .persistent(Codec.STRING)
        .networkSynchronized(ByteBufCodecs.STRING_UTF8)
        .build()).also { COMPONENTS.add(it) }.second

    init {
        COMPONENTS.forEach {
            Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, fromNamespaceAndPath(MODID, it.first), it.second)
        }
    }
}
