package com.github.darkpred.morehitboxes.api;

import com.github.darkpred.morehitboxes.entity.MultiPart;
import com.github.darkpred.morehitboxes.entity.MultiPartEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * The container responsible for creating and managing the hitbox parts. Any entity that implements {@link MultiPartEntity}
 * should create an instance of this via {@link EntityHitboxDataFactory}
 */
public interface EntityHitboxData<T extends Mob & MultiPartEntity<T>> {
    /**
     * Returns the container responsible for creating and managing attack boxes
     */
    AttackBoxData getAttackBoxPlaceHolder();

    @ApiStatus.Internal
    void makeBoundingBoxForCulling();

    /**
     * Returns the calculated culling bounding box. Recommended to call {@link Entity#getBoundingBoxForCulling()} instead
     */
    @ApiStatus.Internal
    AABB getCullingBounds();

    /**
     * Sets the attack bounding box. See {@link MultiPartEntity#makeAttackBoundingBox(float)} for actual calculations
     */
    @ApiStatus.Internal
    void makeAttackBounds();

    /**
     * Returns the calculated attack bounding box, which can be intersected with a targets bounding box to see if it's in range to attack
     *
     * @return a cached version of the calculated attack bounding box
     */
    AABB getAttackBounds();

    /**
     * Returns the horizontal distance from the farthest head part to the mobs center. This value has to be multiplied
     * by {@link Mob#getScale()} if the scale is not 1
     * <p>
     * The returned value could be used to determine a reasonable attack or stop distance when {@link Entity#getBoundingBox()}
     * is smaller than the mobs model
     *
     * @return the scaled distance to the farthest head part
     */
    float getHeadRadius();

    /**
     * Returns {@code true} if at least one {@link MultiPart} has been added
     */
    boolean hasCustomParts();

    /**
     * Returns a list of the parts added to the parent
     *
     * @return the hitbox parts of the parent
     */
    List<MultiPart<T>> getCustomParts();

    /**
     * Returns a hitbox part if the given name was linked in {@link HitboxData#ref()}
     *
     * @param ref the name of the bone the hitbox part is attached to
     * @return the hitbox part attached to the given bone or {@code null} if no part like that was added
     * @apiNote Used by the library to provide optional GeckoLib support
     */
    @Nullable
    MultiPart<T> getCustomPart(String ref);

    @ApiStatus.Internal
    boolean fixPosOnRefresh();
}
