package dev.kleinbox

import dev.kleinbox.client.Inputs
import dev.kleinbox.client.animation.Animations
import net.fabricmc.api.ClientModInitializer

object DancerizerClient : ClientModInitializer {
	/**
	 * Initializes client side only stuff.
	 */
	override fun onInitializeClient() {
		Inputs
		Animations
	}
}