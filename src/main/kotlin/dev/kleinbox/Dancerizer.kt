package dev.kleinbox

import dev.kleinbox.common.SoundEvents
import dev.kleinbox.common.Statistics
import dev.kleinbox.common.item.Components
import dev.kleinbox.common.item.Items
import dev.kleinbox.common.network.Payloads
import net.fabricmc.api.ModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Dancerizer : ModInitializer {
	const val MODID = "dancerizer"
	val logger: Logger = LoggerFactory.getLogger(MODID.replaceFirstChar { it.uppercaseChar() })

	/*
	TODO:
		- [-] Dances:
			- [x] MESMERIZER
			- [x] Pokedance
			- [-] Teto's Growl
		- [x] Taunt from Pizza Tower
			- [ ] Block incoming hit randomly
			- [ ] Particles
		- [ ] Hints (e.g. whenever a key needs to be held down but hasn't)
			- [ ] Make them disable
		- [ ] Make taunt cooldown configurable
	 */
	override fun onInitialize() {
		logger.info("Preparing the dance floor!")

		Payloads
		SoundEvents
		Statistics
		Components
		Items
	}
}