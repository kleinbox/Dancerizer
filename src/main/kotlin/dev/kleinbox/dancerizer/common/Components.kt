package dev.kleinbox.dancerizer.common

import com.mojang.serialization.Codec
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.codec.ByteBufCodecs

@Suppress("UNCHECKED_CAST")
object Components : RegisteringContainer<DataComponentType<out Any>>(BuiltInRegistries.DATA_COMPONENT_TYPE) {

    val DANCE = register("dance", DataComponentType.builder<com.mojang.datafixers.util.Pair<String, Int>>()
        .persistent(Codec.pair(Codec.STRING, Codec.INT))
        .networkSynchronized(ByteBufCodecs.fromCodecWithRegistries(Codec.pair(Codec.STRING, Codec.INT)))
        .build()) as DataComponentType<com.mojang.datafixers.util.Pair<String, Int>>

    val TAUNT = register("taunt", DataComponentType.builder<String>()
        .persistent(Codec.STRING)
        .networkSynchronized(ByteBufCodecs.STRING_UTF8)
        .build()) as DataComponentType<String>
}
