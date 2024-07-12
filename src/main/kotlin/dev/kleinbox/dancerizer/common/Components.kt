package dev.kleinbox.dancerizer.common

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.codec.ByteBufCodecs

@Suppress("UNCHECKED_CAST")
object Components : RegisteringContainer<DataComponentType<out Any>>(BuiltInRegistries.DATA_COMPONENT_TYPE) {

    val DANCE = run {
        val codec = Codec.mapPair(Codec.STRING.fieldOf("dance"), Codec.INT.fieldOf("duration")).codec()
        register("dance", DataComponentType.builder<Pair<String, Int>>()
                .persistent(codec)
                .networkSynchronized(ByteBufCodecs.fromCodecWithRegistries(codec))
                .build()
        ) as DataComponentType<Pair<String, Int>>
    }

    val TAUNT = register("taunt", DataComponentType.builder<String>()
        .persistent(Codec.STRING)
        .networkSynchronized(ByteBufCodecs.STRING_UTF8)
        .build()) as DataComponentType<String>
}
