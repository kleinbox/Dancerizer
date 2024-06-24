package dev.kleinbox.dancerizer.client.config

import dev.kleinbox.dancerizer.Dancerizer.MODID
import dev.kleinbox.dancerizer.common.Config
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.layouts.FrameLayout
import net.minecraft.client.gui.layouts.GridLayout
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component


class ConfigScreen(val config: Config, private val parent: Screen?) : Screen(Component.translatable("$MODID.screen.config")) {

    private var toggleHints = Button.builder(
        Component.translatable("$MODID.options.button.toggle_hints").append(": ").append(
            Component.translatable(if (config.data.client.showHints) "gui.yes" else "gui.no")
        ),
    ) {
        config.data.client.showHints = !config.data.client.showHints
        it.message = Component.translatable("$MODID.options.button.toggle_hints").append(": ").append(
            Component.translatable(if (config.data.client.showHints) "gui.yes" else "gui.no")
        )
    }.tooltip(Tooltip.create(Component.translatable("$MODID.options.button.toggle_hints.tooltip"))).build()

    override fun init() {
        val gridWidget = GridLayout()
        gridWidget.defaultCellSetting().alignHorizontallyCenter().alignVerticallyMiddle()
        val adder: GridLayout.RowHelper = gridWidget.createRowHelper(1)

        adder.addChild(toggleHints)
        adder.addChild(
            Button.builder(
                CommonComponents.GUI_DONE
            ) { this.onClose() }.build(), adder.newCellSettings().paddingTop(6)
        )

        gridWidget.arrangeElements()
        FrameLayout.alignInRectangle(gridWidget, 0, (this.height / 2) - (gridWidget.height / 2), this.width, this.height, 0.5f, 0f)
        gridWidget.visitWidgets(this::addRenderableWidget)
    }

    override fun onClose() {
        config.overwrite()

        if (parent != null) {
            if (this.minecraft != null) minecraft!!.setScreen(parent)
        } else super.onClose()
    }

    override fun isPauseScreen(): Boolean = false
}