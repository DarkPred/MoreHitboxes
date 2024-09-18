package com.github.darkpred.multipartsupport.entity;

import net.minecraft.world.entity.EntityType;

import java.util.List;

public interface MultiPartEntity {
    Store getStore();

    void spawnHitBoxes(List<EntityHitboxManager.Hitbox> hitboxes, EntityType<? extends Example> entityType);

    default boolean isCustomMultiPart() {
        //TODO: Probably should use mixin instead
        return getStore().isCustomMultiPart();
    }
}
