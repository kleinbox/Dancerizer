package dev.kleinbox.common;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@SuppressWarnings("unused")
public interface IExpressivePlayer {
    long dancerizer$getLastEmoteTimestamp();
    void dancerizer$setLastEmoteTimestamp(long lastEmoteTimestamp);

    void dancerizer$taunt();

    int dancerizer$isTaunting();

    @Environment(EnvType.CLIENT)
    boolean dancerizer$wasPlaying();
    @Environment(EnvType.CLIENT)
    void dancerizer$setPlaying(boolean state);
}
