package dev.kleinbox.dancerizer.client

import com.mojang.blaze3d.platform.InputConstants
import dev.kleinbox.dancerizer.Dancerizer.MODID
import dev.kleinbox.dancerizer.Dancerizer.logger
import dev.kleinbox.dancerizer.client.animation.Animations
import dev.kleinbox.dancerizer.common.Components
import dev.kleinbox.dancerizer.common.ExpressivePlayer
import dev.kleinbox.dancerizer.common.item.GroovingTrinket
import dev.kleinbox.dancerizer.common.payload.DanceTimestampPayload
import dev.kleinbox.dancerizer.common.payload.TauntPayload
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.KeyMapping
import net.minecraft.client.KeyMapping.CATEGORY_GAMEPLAY
import net.minecraft.client.KeyMapping.CATEGORY_MULTIPLAYER
import net.minecraft.client.Minecraft
import org.lwjgl.glfw.GLFW

/**
 * Contains all Key Mappings with their corresponding logic.
 */
@Suppress("unused")
object Inputs {
    private const val DANCERIZER_CATEGORY = "key.categories.dancerizer"
    private val actions = HashMap<KeyMapping, (client: Minecraft) -> Unit>()

    val dance = action(KeyBindingHelper.registerKeyBinding(KeyMapping(
        "$MODID.keybind.dance",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_Y,
        DANCERIZER_CATEGORY
    ))!!) { client: Minecraft ->

        if (isNotInWorld(client))
            return@action

        // Validate animations
        val dances = GroovingTrinket.gatherItemWithDance(client.player!! as ExpressivePlayer)
        dances.forEach {
            val animation = it.components.get(Components.DANCE)
            if (animation != null && !Animations.poses.contains(animation.first))
                logger.warn("Animation for dance '${animation}' is not present but could be requested")
        }

        ClientPlayNetworking.send(DanceTimestampPayload)
    }

    val taunt = action(KeyBindingHelper.registerKeyBinding(KeyMapping(
        "$MODID.keybind.taunt",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_G,
        DANCERIZER_CATEGORY
    ))!!) { client: Minecraft ->

        if (isNotInWorld(client))
            return@action

        // Validate animations
        val taunts = GroovingTrinket.gatherItemWithDance(client.player!! as ExpressivePlayer)
        taunts.forEach {
            val animation = it.components.get(Components.TAUNT)
            if (animation != null && !Animations.poses.contains(animation))
                logger.warn("Animation for taunt '${animation}' is not present but could be requested")
        }

        ClientPlayNetworking.send(TauntPayload)
    }

    init {
        ClientTickEvents.END_CLIENT_TICK.register { client: Minecraft ->
            actions.forEach {
                if (it.key.isDown)
                    it.value.invoke(client)
            }
        }
    }

    private fun action(input: KeyMapping, response: (client: Minecraft) -> Unit): KeyMapping {
        actions[input] = response
        return input
    }

    private fun isNotInWorld(client: Minecraft): Boolean
        = (client.player == null || client.player!!.hasContainerOpen() || client.player!!.vehicle != null)
}