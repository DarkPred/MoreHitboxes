package com.github.darkpred.morehitboxes.internal;

import com.github.darkpred.morehitboxes.api.MultiPart;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * Mixin interface injected into {@link net.minecraft.world.phys.EntityHitResult}. Any call to
 * {@link net.minecraft.world.entity.projectile.ProjectileUtil#getEntityHitResult} will most likely return an instance of this
 * where {@link EntityHitResult#getEntity()} will be the actual mob
 */
@ApiStatus.NonExtendable
public interface MultiPartEntityHitResult {

    void moreHitboxes$setMultiPart(MultiPart<?> part);

    /**
     * Returns the {@link MultiPart} that was originally the entity of the hit result
     *
     * @return the {@link MultiPart} targeted
     */
    @Nullable
    @Contract(pure = true)
    MultiPart<?> moreHitboxes$getMultiPart();
}
