package com.github.darkpred.multipartsupport.entity;

import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Mixin interface injected into {@link net.minecraft.world.phys.EntityHitResult}. Any call to
 * {@link net.minecraft.world.entity.projectile.ProjectileUtil#getEntityHitResult} will most likely return an instance of this
 * where {@link EntityHitResult#getEntity()} will be the actual mob
 */
@ApiStatus.NonExtendable
public interface MultiPartEntityHitResult {

    void multiPartSupport$setMultiPart(MultiPart<?> part);

    /**
     * Returns the {@link MultiPart} that was originally the entity of the hit result
     *
     * @return the {@link MultiPart} targeted
     */
    @Nullable
    MultiPart<?> multiPartSupport$getMultiPart();
}
