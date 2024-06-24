package dev.kleinbox.dancerizer

import dev.kleinbox.dancerizer.client.Inputs
import dev.kleinbox.dancerizer.client.animation.Animations
import dev.kleinbox.dancerizer.client.renderer.GroovingEquipmentRenderer
import dev.kleinbox.dancerizer.common.item.Items
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics

/**
 * Initializes client side only stuff.
 */
object DancerizerClient : ClientModInitializer {
	override fun onInitializeClient() {
		Inputs
		Animations

		ArmorRenderer.register(GroovingEquipmentRenderer(), Items.ATHLETIC_WINGS)
		ArmorRenderer.register(GroovingEquipmentRenderer(), Items.RRONGS_TIE)
	}
}