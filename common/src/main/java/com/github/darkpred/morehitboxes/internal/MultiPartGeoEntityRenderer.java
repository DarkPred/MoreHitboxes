package com.github.darkpred.morehitboxes.internal;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;

/**
 * Mixin interface injected into GeoEntityRenderer.
 */
@ApiStatus.NonExtendable
@ApiStatus.Internal
public interface MultiPartGeoEntityRenderer {

    @ApiStatus.Internal
    void moreHitboxes$removeTickForEntity(Entity entity);
}
