import java.time.LocalDate

object Constants {
	// Mod Properties
	const val VERSION = "1.0.0"
	const val GROUP = "dev.kleinbox"
	const val MODID = "dancerizer"

	// Description
	val NAME = MODID.replaceFirstChar { it.uppercase() }
	val AUTHORS: List<String> = listOf(
		"Sammy L. Koch"
	)
	val DESCRIPTION = """
		Lets you perform taunts and whole dances with new accessories.
		This has been made for §aModFest: Carnival§r§7! Check it out! §o§8(Linked down below)§r§7
		
		§f§nCopyright © ${if (LocalDate.now().year > 2024) "2024-${LocalDate.now()}" else "2024"} ${AUTHORS[0]}§r§7
		This is free software.  You may redistribute copies of it under the terms of the GNU Lesser General Public License, version 3 only §8<See link down below for more details>.§r§7
		There is NO WARRANTY, to the extent permitted by law.
	""".trimIndent()

	// Fabric Properties
	const val MINECRAFT_VERSION = "1.21"
	const val LOADER_VERSION = "0.15.11"
	const val KOTLIN_VERSION = "1.11.0+kotlin.2.0.0"

	// Dependencies
	const val JSON_VERSION = "1.7.0"
	const val TOML_VERSION = "0.4.0"

	// Mod Dependencies
	const val FABRIC_VERSION = "0.100.1+1.21"
	const val MODMENU_VERSION = "11.0.0-beta.1"
	const val CONFETTI_VERSION = "1.0.0+1.21"

	// Runtime only
	const val COMPONENTVIEWER_VERSION = "1.1.2+1.21"
}

plugins {
	id("fabric-loom") version "1.7.1"
	kotlin("jvm") version "2.0.0"
	kotlin("plugin.serialization") version "2.0.0"
}

base {
	archivesName.set(Constants.MODID)
	version = Constants.VERSION
	group = Constants.GROUP
}

loom {
	splitEnvironmentSourceSets()

	mods {
		register(Constants.MODID) {
			sourceSet(sourceSets["main"])
			sourceSet(sourceSets["client"])
		}
	}
}

repositories {
	exclusiveContent {
		forRepository {
			maven {
				name = "Modrinth"
				url = uri("https://api.modrinth.com/maven")
			}
		}

		filter {
			includeGroup("maven.modrinth")
		}
	}
	mavenCentral()
}

dependencies {
	minecraft("com.mojang", "minecraft", Constants.MINECRAFT_VERSION)
	mappings(loom.officialMojangMappings())
	modImplementation("net.fabricmc", "fabric-loader", Constants.LOADER_VERSION)

	modImplementation("net.fabricmc.fabric-api", "fabric-api", Constants.FABRIC_VERSION)
	modImplementation("net.fabricmc", "fabric-language-kotlin", Constants.KOTLIN_VERSION)
	modApi("maven.modrinth","modmenu", Constants.MODMENU_VERSION)

	modLocalRuntime("maven.modrinth", "component-viewer", Constants.COMPONENTVIEWER_VERSION)
	modLocalRuntime("maven.modrinth","modmenu", Constants.MODMENU_VERSION)

	implementation("org.jetbrains.kotlinx", "kotlinx-serialization-json", Constants.JSON_VERSION)
	implementation("net.peanuuutz.tomlkt", "tomlkt", Constants.TOML_VERSION)
}

tasks.processResources {
	inputs.property("version", Constants.VERSION)

	filesMatching("fabric.mod.json") {
		expand(mapOf(
			"fabricloader" to Constants.LOADER_VERSION,
			"minecraft" to Constants.MINECRAFT_VERSION,
			"fabricapi" to Constants.FABRIC_VERSION,
			"fabriclanguagekotlin" to Constants.KOTLIN_VERSION,
			"confetti" to Constants.CONFETTI_VERSION,

			"modid" to Constants.MODID,
			"version" to Constants.VERSION,
			"name" to Constants.NAME,
			"description" to Constants.DESCRIPTION,
			"author_0" to Constants.AUTHORS[0]
		))
	}
}

java {
	withSourcesJar()

	sourceCompatibility = JavaVersion.VERSION_21
	targetCompatibility = JavaVersion.VERSION_21
}

tasks {
	jar {
		from("COPYING.md") {
			rename { "${it}_${project.base.archivesName.get()}" }
		}
	}
}