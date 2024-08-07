package dev.kleinbox.dancerizer.common.mixin;

import dev.kleinbox.dancerizer.common.Components;
import dev.kleinbox.dancerizer.common.ExpressivePlayer;
import dev.kleinbox.dancerizer.common.item.GroovingTrinket;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(LivingEntity.class)
public abstract class AutoTauntingPlayerMixin {

    @Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSource;getEntity()Lnet/minecraft/world/entity/Entity;"), cancellable = true)
    public void dancerizer$hurt(DamageSource damageSource, float f, CallbackInfoReturnable<Boolean> cir) {
        if (!(this instanceof ExpressivePlayer taunter))
            return;

        @SuppressWarnings("DataFlowIssue") // We are professionals here
        Player player = (Player) taunter;
        if (player.isCreative() || player.isSpectator())
            return;

        List<ItemStack> taunts = GroovingTrinket.Companion.gatherItemWithTaunt(taunter).stream().toList();
        float chance = (100f / 5) * taunts.size() - 10;
        float calc = (Mth.randomBetween(player.getRandom(), 0f, 1f) * 100);
        
        if (chance > 0 && calc <= chance) {
            ItemStack itemStack = taunts.get(Mth.randomBetweenInclusive(player.getRandom(), 0, taunts.size() - 1));
            if (itemStack != null) {
                String taunt = itemStack.getComponents().get(Components.INSTANCE.getTAUNT());
                if (taunt != null) {
                    taunter.dancerizer$reset();
                    taunter.dancerizer$setTaunt(taunt, true);
                    cir.setReturnValue(false);
                    cir.cancel();
                }
            }
        }
    }
}
