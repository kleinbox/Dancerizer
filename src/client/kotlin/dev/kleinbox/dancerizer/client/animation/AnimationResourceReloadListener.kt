package dev.kleinbox.dancerizer.client.animation

import dev.kleinbox.dancerizer.Dancerizer.MODID
import dev.kleinbox.dancerizer.Dancerizer.logger
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromStream
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.PreparableReloadListener
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.util.profiling.ProfilerFiller
import java.io.InputStream
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

object AnimationResourceReloadListener : IdentifiableResourceReloadListener {

    override fun getFabricId(): ResourceLocation = ResourceLocation.fromNamespaceAndPath(MODID, "animations")

    override fun reload(
        synchronizer: PreparableReloadListener.PreparationBarrier,
        manager: ResourceManager,
        prepareProfiler: ProfilerFiller,
        applyprofiler: ProfilerFiller,
        prepareExecutor: Executor,
        applyExecutor: Executor
    ): CompletableFuture<Void> {
        Animations.poses.clear()
        val animations: MutableMap<String, HumanoidPoseManipulator> = mutableMapOf()

        return CompletableFuture
            .runAsync({
                prepareAnimations(manager, animations)
            }, prepareExecutor)

            .thenCompose(synchronizer::wait).thenRunAsync({
                applyAnimations(animations)
            }, applyExecutor)
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun prepareAnimations(manager: ResourceManager, animations: MutableMap<String, HumanoidPoseManipulator>) {
        manager.listResources("animations") { it.path.endsWith(".json") }.entries
            .forEach { resource ->
                val stream: InputStream = manager.getResource(resource.key).get().open()

                try {
                    val json = Json.decodeFromStream<JsonObject>(stream)

                    var poses = HumanoidPoseManipulator.extract(resource.key.path, json)
                    poses = poses.mapKeys { entry -> "${resource.key.path.removeSuffix(".json").removePrefix("animations/")}.${entry.key}" }

                    animations.putAll(poses)
                } catch (e: Exception) {
                    logger.error("Error occurred while loading '${resource.key.path}'", e)
                }

                stream.close()
            }
    }

    private fun applyAnimations(preparedData: Map<String, HumanoidPoseManipulator>) {
        Animations.poses.putAll(preparedData)

        val total = preparedData.count()
        logger.info("Gathered $total animation${if (total == 1) "" else "s"}")
    }
}