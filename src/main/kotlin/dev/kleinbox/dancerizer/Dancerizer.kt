package dev.kleinbox.dancerizer

import dev.kleinbox.dancerizer.common.*
import dev.kleinbox.dancerizer.common.compat.ConfettiEmitter
import dev.kleinbox.dancerizer.common.compat.ConfettiEmitterImplementation
import dev.kleinbox.dancerizer.common.item.ItemCategories
import dev.kleinbox.dancerizer.common.item.Items
import dev.kleinbox.dancerizer.common.payload.Payloads
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Dancerizer : ModInitializer {
	const val MODID = "dancerizer"
	val logger: Logger = LoggerFactory.getLogger(MODID.replaceFirstChar { it.uppercaseChar() })

	var config = Config()
	var confetti_emitter: ConfettiEmitter = ConfettiEmitter.Companion.Dummy

	override fun onInitialize() {
		logger.info("Preparing the dance floor!")

		if (FabricLoader.getInstance().isModLoaded("confetti"))
			confetti_emitter = ConfettiEmitterImplementation

		PlayerExtendedData
		TauntDamageDenial
		Payloads
		SoundEvents
		Statistics
		Components
		Items
		ItemCategories
		LootTableModifier
	}
}