package dev.kleinbox.dancerizer

import dev.kleinbox.dancerizer.common.*
import dev.kleinbox.dancerizer.common.Config
import dev.kleinbox.dancerizer.common.item.ItemCategories
import dev.kleinbox.dancerizer.common.item.Items
import dev.kleinbox.dancerizer.common.payload.Payloads
import net.fabricmc.api.ModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Dancerizer : ModInitializer {
	const val MODID = "dancerizer"
	val logger: Logger = LoggerFactory.getLogger(MODID.replaceFirstChar { it.uppercaseChar() })

	var config = Config()

	override fun onInitialize() {
		logger.info("Preparing the dance floor!")

		Payloads
		SoundEvents
		Statistics
		Components
		Items
		ItemCategories
		LootTableModifier
	}
}