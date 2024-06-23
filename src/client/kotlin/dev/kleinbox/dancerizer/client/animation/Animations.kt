package dev.kleinbox.dancerizer.client.animation

import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.server.packs.PackType

object Animations {

    val poses: MutableMap<String, HumanoidPoseManipulator> = mutableMapOf()

    init {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES)
            .registerReloadListener(AnimationResourceReloadListener)
    }
}