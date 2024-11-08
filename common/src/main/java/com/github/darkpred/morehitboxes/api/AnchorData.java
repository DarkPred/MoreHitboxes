package com.github.darkpred.morehitboxes.api;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;

/**
 * The container responsible for creating and managing anchor positions. Each hitbox that has {@link HitboxData#isAnchor()}
 * set will be handled here
 */
public interface AnchorData {

    /**
     * Returns an anchor position if the given name was linked in {@link HitboxData#ref()}.
     * <p>
     * If GeckoLib support is enabled this position will match the referenced bone
     *
     * @param ref the name of the anchor
     * @return the anchor position in the world or an empty {@code Optional}
     */
    Optional<Vec3> getAnchorPos(String ref);

    /**
     * Returns {@code true} if an anchor is referenced by the given string
     */
    boolean isAnchor(String ref);

    @ApiStatus.Internal
    void addAnchor(String ref, HitboxData hitboxData);

    @ApiStatus.Internal
    void updatePositions();

    @ApiStatus.Internal
    void updatePosition(String ref, Vec3 pos);
}
