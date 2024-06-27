package dev.kleinbox.dancerizer.common.mixin;

import dev.kleinbox.dancerizer.common.Components;
import dev.kleinbox.dancerizer.common.ExpressivePlayer;
import dev.kleinbox.dancerizer.common.item.GroovingTrinket;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Player.class)
public abstract class AutoTauntingPlayerMixin extends LivingEntity {

    protected AutoTauntingPlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    public void dancerizer$hurt(DamageSource damageSource, float f, CallbackInfoReturnable<Boolean> cir) {
        ExpressivePlayer player = (ExpressivePlayer) this;
        List<ItemStack> taunts = GroovingTrinket.Companion.gatherItemWithTaunt(player).stream().toList();
        float chance = (100f / 5) * taunts.size() - 10;
        float calc = (Mth.randomBetween(this.getRandom(), 0f, 1f) * 100);
        
        if (chance > 0 && calc <= chance) {
            ItemStack itemStack = taunts.get(Mth.randomBetweenInclusive(this.getRandom(), 0, taunts.size() - 1));
            if (itemStack != null) {
                String taunt = itemStack.getComponents().get(Components.INSTANCE.getTAUNT());
                if (taunt != null) {
                    player.dancerizer$setTaunt(taunt, true);
                    cir.setReturnValue(false);
                    cir.cancel();
                }
            }
        }
    }
}
