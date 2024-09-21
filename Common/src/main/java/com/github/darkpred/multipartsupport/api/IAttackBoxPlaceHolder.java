package com.github.darkpred.multipartsupport.api;

import com.github.darkpred.multipartsupport.entity.EntityHitboxManager;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import software.bernie.geckolib3.core.builder.Animation;

/**
 * The container responsible for creating and managing attack boxes. See {@link com.github.darkpred.multipartsupport.entity.EntityHitboxManager.HitboxData}
 * for more information
 */
public interface IAttackBoxPlaceHolder {

    @ApiStatus.Internal
    void addAttackBox(String ref, EntityHitboxManager.HitboxData hitboxData);

    /**
     * Returns a hitbox part if the given name was linked in {@link EntityHitboxManager.HitboxData#ref()}.
     * <p>
     * Used by the library to provide optional GeckoLib support
     *
     * @param ref the name of the bone the hitbox part is attached to
     * @return the hitbox part attached to the given bone
     */
    EntityHitboxManager.HitboxData getAttackBox(String ref);

    /**
     * Sets the position of a currently active attack box
     *
     * @param attackBox the attack box to be moved
     * @param worldPos  the new position relative to the world
     */
    void moveActiveAttackBox(EntityHitboxManager.HitboxData attackBox, Vec3 worldPos);

    /**
     * Returns {@code true} if the given attack box will trigger {@link com.github.darkpred.multipartsupport.entity.MultiPartEntity#attackBoxHit(LocalPlayer) MultiPartEntity#attackBoxHit(LocalPlayer)}
     */
    boolean isAttackBoxActive(EntityHitboxManager.HitboxData attackBox);

    /**
     * Activates all attack boxes for a given duration
     * <p>
     * If GeckoLib is enabled, call this function at the beginning of an attack with {@link Animation#animationLength}
     * as the duration
     *
     * @param attackDuration for how long(in ticks) the attack should be active
     */
    void activateAttackBoxes(ClientLevel level, double attackDuration);

    @ApiStatus.Internal
    void clientTick(ClientLevel level);

    /**
     * The last tick of the attack
     */
    long attackBoxEndTime();
}
