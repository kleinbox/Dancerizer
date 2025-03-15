package dev.kleinbox.dancerizer.common.api.v1;

import dev.kleinbox.dancerizer.common.PlayerExtendedData;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;

/**
 * Callback for players performing a dance.
 * Called before or after an animation has completed.
 * Upon returns:
 * - SUCCESS    cancels further processing and continues with starting the animation, if being fired at beginning
 * - PASS       falls back to further processing and defaults to SUCCESS if no other listeners are available
 * - FAIL       cancels further processing and does not start the animation, if being fired at beginning
 */
public interface PlayerAnimationCallback {
    Event<PlayerAnimationCallback> EVENT = EventFactory.createArrayBacked(PlayerAnimationCallback.class,
            (listeners) -> (player, data, status) -> {
                for (PlayerAnimationCallback listener : listeners) {
                    InteractionResult result = listener.interact(player, data, status);

                    if (result != InteractionResult.PASS)
                        return result;
                }

                return InteractionResult.PASS;
            });

    InteractionResult interact(Player player, PlayerExtendedData data, PlayerAnimationStatus status);
}
