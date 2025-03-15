package dev.kleinbox.dancerizer.common.api.v1;

/**
 * Represents a players dance or taunt status in {PlayerAnimationCallback}.
 *
 * @param type Whenever the player is taunting or dancing.
 * @param duration Represents the animations duration, measured in ticks. Will be 0 if the animation has completed.
 * @param id The kind of animation that is or has been used.
 */
public record PlayerAnimationStatus(TYPE type, int duration, String id) {
    public enum TYPE {
        TAUNTING,
        DANCING
    }
}
