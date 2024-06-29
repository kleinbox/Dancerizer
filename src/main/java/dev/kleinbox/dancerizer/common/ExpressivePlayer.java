package dev.kleinbox.dancerizer.common;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public interface ExpressivePlayer {

    Inventory dancerizer$inventory();
    ItemStack dancerizer$mainHandItem();

    long dancerizer$getLastEmoteTimestamp();
    void dancerizer$setLastEmoteTimestamp(long lastEmoteTimestamp, @NotNull Pair<String, Integer> dance);

    void dancerizer$setTaunt(@NotNull String taunt, boolean force);
    int dancerizer$isTaunting();

    int dancerizer$isDancePlaying();
    void dancerizer$setDancePlaying(int duration);

    String dancerizer$getAnimationPose();

    boolean dancerizer$wasDancingOrTaunting();
    void dancerizer$setWasDancingOrTaunting(boolean value);
}
