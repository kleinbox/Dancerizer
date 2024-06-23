package dev.kleinbox.dancerizer

import dev.kleinbox.dancerizer.common.Components
import dev.kleinbox.dancerizer.common.ItemCategories
import dev.kleinbox.dancerizer.common.SoundEvents
import dev.kleinbox.dancerizer.common.Statistics
import dev.kleinbox.dancerizer.common.item.Items
import dev.kleinbox.dancerizer.common.payload.Payloads
import net.fabricmc.api.ModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Dancerizer : ModInitializer {
	const val MODID = "dancerizer"
	val logger: Logger = LoggerFactory.getLogger(MODID.replaceFirstChar { it.uppercaseChar() })

	/*
	TODO:
		- [x] Dances:
			- [x] MESMERIZER
			- [x] Pokedance
			- [ ] One last dance maybe
		- [-] Taunts:
			- [x] Block incoming hit randomly
			- [ ] Particles
		- [x] Hints (e.g. whenever a key needs to be held down but hasn't)
			- [ ] Being able to disable them would be kinda nice
		- [ ] Make taunt cooldown configurable
	 */
	override fun onInitialize() {
		logger.info("Preparing the dance floor!")

		Payloads
		SoundEvents
		Statistics
		Components
		Items
		ItemCategories
	}
}