package dev.kleinbox.dancerizer.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer
import net.minecraft.client.Minecraft
import net.minecraft.client.model.HumanoidModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.LivingEntityRenderer
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack

class GroovingEquipmentRenderer : ArmorRenderer {
    override fun render(
        matrices: PoseStack,
        vertexConsumers: MultiBufferSource,
        stack: ItemStack,
        entity: LivingEntity,
        slot: EquipmentSlot,
        light: Int,
        contextModel: HumanoidModel<LivingEntity>
    ) {
        val itemRenderer = Minecraft.getInstance().itemRenderer

        when (slot) {
            EquipmentSlot.CHEST -> {
                val model = itemRenderer.getModel(
                    stack,
                    entity.level(),
                    entity,
                    light
                )

                if (model != null) {

                    matrices.pushPose()

                    contextModel.body.translateAndRotate(matrices)
                    matrices.scale(1f, -1f, 1f)

                    itemRenderer.renderStatic(
                        entity,
                        stack,
                        ItemDisplayContext.HEAD,
                        false,
                        matrices,
                        vertexConsumers,
                        entity.level(),
                        light,
                        LivingEntityRenderer.getOverlayCoords(entity, 0.0F),
                        entity.id
                    )

                    matrices.popPose()
                }
            }
            else -> {}
        }
    }
}