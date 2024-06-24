package dev.kleinbox.dancerizer.client.mixin;

import dev.kleinbox.dancerizer.Dancerizer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class PreventMessageGuiMixin {

    @Shadow
    private Component overlayMessageString;

    @Inject(method = "renderOverlayMessage", at = @At("HEAD"), cancellable = true)
    private void renderOverlayMessage(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (overlayMessageString != null) {
            ComponentContents raw = overlayMessageString.getContents();
            if (raw instanceof TranslatableContents translatable && !Dancerizer.INSTANCE.getConfig().getData().getClient().getShowHints()) {
                if (translatable.getKey().equals("info." + Dancerizer.MODID + ".missing_dance")
                        || translatable.getKey().equals("info." + Dancerizer.MODID + ".missing_taunt")) {
                    ci.cancel();
                }
            }
        }
    }
}
