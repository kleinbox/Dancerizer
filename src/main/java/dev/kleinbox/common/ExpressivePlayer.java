package dev.kleinbox.common;

import com.mojang.datafixers.util.Pair;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public interface ExpressivePlayer {
    long dancerizer$getLastEmoteTimestamp();
    void dancerizer$setLastEmoteTimestamp(long lastEmoteTimestamp, @NotNull Pair<String, Integer> dance);

    void dancerizer$setTaunt(@NotNull String taunt);
    int dancerizer$isTaunting();

    int dancerizer$isDancePlaying();
    void dancerizer$setDancePlaying(int duration);

    String dancerizer$getAnimationPose();
}
