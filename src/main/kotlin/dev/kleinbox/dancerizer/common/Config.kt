package dev.kleinbox.dancerizer.common

import dev.kleinbox.dancerizer.Dancerizer.MODID
import dev.kleinbox.dancerizer.Dancerizer.logger
import kotlinx.serialization.*
import net.fabricmc.loader.api.FabricLoader
import net.peanuuutz.tomlkt.Toml
import net.peanuuutz.tomlkt.TomlComment
import java.io.File

class Config {
    companion object {
        val LOCATION = File(FabricLoader.getInstance().configDir.toString(), "$MODID.toml")
        val DEFAULT = """
            [client]
            showHints = true
            capeDuringAnimations = true
        
            [server]
            tauntCooldown = 20
            tauntDuration = 5
        """.trimIndent()

        @Serializable
        data class Data(
            val client: ClientData,
            val server: ServerData
        ) {
            @Serializable
            data class ClientData(
                @TomlComment("""
                    Whenever a small text should appear after
                    pressing a hotkey related to a taunt or dance
                """)
                var showHints: Boolean,
                @TomlComment("""
                    Toggles the player cape during animations
                """)
                var capeDuringAnimations: Boolean
            )

            @Serializable
            data class ServerData(
                @TomlComment("How long a player needs to wait until they can manually taunt again")
                val tauntCooldown: Short,
                @TomlComment("""
                    How long a taunt is being shown
                    (Does not affect damage prevention)
                """)
                val tauntDuration: Int,
            )
        }

        val toml = Toml {
            ignoreUnknownKeys = true
        }
    }

    private var saveEnv = false
    private var raw = runCatching { LOCATION.readText() }.getOrElse { DEFAULT }
    @OptIn(ExperimentalSerializationApi::class)
    val data = kotlin.runCatching { toml.decodeFromString<Data>(raw) }.getOrElse {
        if (it is MissingFieldException) {
            logger.warn("The following field${if (it.missingFields.size > 1) "s are" else " is"} missing in the config: ${it.missingFields}; Using default config during this session")
            saveEnv = true
        } else {
            logger.error("Config seems to be invalid; Generating default config")
        }
        toml.decodeFromString<Data>(DEFAULT)
    }

    init { overwrite() }

    fun overwrite() {
        raw = toml.encodeToString<Data>(data)
        if (!saveEnv)
            LOCATION.writeText(raw)
    }
}