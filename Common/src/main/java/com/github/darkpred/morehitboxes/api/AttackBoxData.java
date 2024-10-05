package com.github.darkpred.morehitboxes.api;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

/**
 * The container responsible for creating and managing attack boxes. Each hitbox that has {@link HitboxData#isAttackBox()}
 * set will be activated when {@link #activateAttackBoxes(Level, double)} is called.
 * <p>
 * For now this is entirely client side
 */
public interface AttackBoxData {

    @ApiStatus.Internal
    void addAttackBox(String ref, HitboxData hitboxData);

    /**
     * Returns a hitbox part if the given name was linked in {@link HitboxData#ref()}.
     * <p>
     * Used by the library to provide optional GeckoLib support
     *
     * @param ref the name of the bone the hitbox part is attached to
     * @return the hitbox part attached to the given bone
     */
    HitboxData getAttackBox(String ref);

    /**
     * Sets the position of a currently active attack box. Will be called by the library if GeckoLib is installed
     *
     * @param attackBox the attack box to be moved
     * @param worldPos  the new position relative to the world
     */
    void moveActiveAttackBox(HitboxData attackBox, Vec3 worldPos);

    /**
     * Returns {@code true} if the given attack box will trigger {@link com.github.darkpred.morehitboxes.entity.MultiPartEntity#attackBoxHit(Player) MultiPartEntity#attackBoxHit(Player)}
     */
    boolean isAttackBoxActive(HitboxData attackBox);

    /**
     * Activates all attack boxes for a given duration
     * <p>
     * If GeckoLib is enabled, call this function at the beginning of an attack with {@link software.bernie.geckolib3.core.builder.Animation#animationLength}
     * as the duration
     *
     * @param attackDuration for how long(in ticks) the attack should be active
     */
    void activateAttackBoxes(Level level, double attackDuration);

    @ApiStatus.Internal
    void clientTick(Level level);

    @ApiStatus.Internal
    Map<HitboxData, Vec3> getActiveBoxes();

    /**
     * The last tick of the attack
     */
    long attackBoxEndTime();
}
