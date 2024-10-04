package com.github.darkpred.morehitboxes.api;

import com.github.darkpred.morehitboxes.entity.HitboxDataLoader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import software.bernie.geckolib3.core.builder.Animation;

import java.util.Map;

/**
 * The container responsible for creating and managing attack boxes. See {@link HitboxDataLoader.HitboxData}
 * for more information
 */
public interface AttackBoxData {

    @ApiStatus.Internal
    void addAttackBox(String ref, HitboxDataLoader.HitboxData hitboxData);

    /**
     * Returns a hitbox part if the given name was linked in {@link HitboxDataLoader.HitboxData#ref()}.
     * <p>
     * Used by the library to provide optional GeckoLib support
     *
     * @param ref the name of the bone the hitbox part is attached to
     * @return the hitbox part attached to the given bone
     */
    HitboxDataLoader.HitboxData getAttackBox(String ref);

    /**
     * Sets the position of a currently active attack box
     *
     * @param attackBox the attack box to be moved
     * @param worldPos  the new position relative to the world
     */
    void moveActiveAttackBox(HitboxDataLoader.HitboxData attackBox, Vec3 worldPos);

    /**
     * Returns {@code true} if the given attack box will trigger {@link com.github.darkpred.morehitboxes.entity.MultiPartEntity#attackBoxHit(Player) MultiPartEntity#attackBoxHit(Player)}
     */
    boolean isAttackBoxActive(HitboxDataLoader.HitboxData attackBox);

    /**
     * Activates all attack boxes for a given duration
     * <p>
     * If GeckoLib is enabled, call this function at the beginning of an attack with {@link Animation#animationLength}
     * as the duration
     *
     * @param attackDuration for how long(in ticks) the attack should be active
     */
    void activateAttackBoxes(Level level, double attackDuration);

    @ApiStatus.Internal
    void clientTick(Level level);

    @ApiStatus.Internal
    Map<HitboxDataLoader.HitboxData, Vec3> getActiveBoxes();

    /**
     * The last tick of the attack
     */
    long attackBoxEndTime();
}
