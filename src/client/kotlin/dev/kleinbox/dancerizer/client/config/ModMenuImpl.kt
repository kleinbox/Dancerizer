package dev.kleinbox.dancerizer.client.config

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import dev.kleinbox.dancerizer.Dancerizer
import net.minecraft.client.gui.screens.Screen

@Suppress("unused")
class ModMenuImpl : ModMenuApi {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<ConfigScreen> {
        return ConfigScreenFactory<ConfigScreen> { parent -> ConfigScreen(Dancerizer.config, parent) }
    }
}
