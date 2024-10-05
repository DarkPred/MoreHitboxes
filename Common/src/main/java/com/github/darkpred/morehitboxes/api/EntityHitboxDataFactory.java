package com.github.darkpred.morehitboxes.api;

import com.github.darkpred.morehitboxes.internal.EntityHitboxDataInternal;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

/**
 * @see MultiPartEntity
 */
public class EntityHitboxDataFactory {
    private EntityHitboxDataFactory() {

    }

    /**
     * Creates a new {@link EntityHitboxData} for the given entity
     *
     * @param entity           the entity
     * @param fixPosOnRefresh  if {@code true} the entities y position will be saved before and applied after a {@link Entity#refreshDimensions() refreshDimensions} call.
     *                         This can prevent odd displacement in certain scenarios
     * @param usesAttackBounds whether {@link MultiPartEntity#makeAttackBoundingBox(float)} should be called
     * @return a new {@link EntityHitboxData} instance
     */
    public static <T extends Mob & MultiPartEntity<T>> EntityHitboxData<T> create(T entity, boolean fixPosOnRefresh, boolean usesAttackBounds) {
        return new EntityHitboxDataInternal<>(entity, fixPosOnRefresh, usesAttackBounds);
    }

    /**
     * Creates a new {@link EntityHitboxData} for the given entity with attack bounds and fixPosOnRefresh enabled
     *
     * @param entity the entity
     * @return a new {@link EntityHitboxData} instance
     */
    public static <T extends Mob & MultiPartEntity<T>> EntityHitboxData<T> create(T entity) {
        return create(entity, true, true);
    }
}
