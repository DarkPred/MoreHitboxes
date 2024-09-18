package com.github.darkpred.multipartsupport.entity;

import com.github.darkpred.multipartsupport.platform.Services;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

public interface MultiPart<T extends Mob & MultiPartEntity> {

    T getParent();

    Entity getEntity();

    Vec3 getOffset();

    void setOverride(AnimationOverride animationOverride);

    AnimationOverride getOverride();

    interface Factory {

        Factory INSTANCE = Services.load(Factory.class);

        <T extends Mob & MultiPartEntity> MultiPart<T> create(T parent, EntityHitboxManager.Hitbox hitbox);
    }
}
