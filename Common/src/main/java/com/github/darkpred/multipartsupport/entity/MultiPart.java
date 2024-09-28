package com.github.darkpred.multipartsupport.entity;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

/**
 * @param <T>
 *///TODO: Javadoc(mention PartEntity)
public interface MultiPart<T extends Mob & MultiPartEntity<T>> {

    String getPartName();

    T getParent();

    Entity getEntity();

    Vec3 getOffset();

    void setOverride(AnimationOverride animationOverride);

    AnimationOverride getOverride();

    default void updatePosition() {
        Entity entity = getEntity();
        entity.xo = entity.getX();
        entity.yo = entity.getY();
        entity.zo = entity.getZ();
        entity.xOld = entity.xo;
        entity.yOld = entity.yo;
        entity.zOld = entity.zo;
        Vec3 offset = getOffset();
        AnimationOverride animationOverride = getOverride();
        Vec3 newPos;
        if (animationOverride != null) {
            newPos = getParent().position().add(animationOverride.localPos());
        } else {
            newPos = getParent().position().add(new Vec3(offset.x, offset.y, offset.z).yRot(-getParent().yBodyRot * Mth.DEG_TO_RAD).scale(getParent().getScale()));
        }
        entity.setPos(newPos);
    }

    @ApiStatus.Internal
    interface Factory {
        <T extends Mob & MultiPartEntity<T>> MultiPart<T> create(T parent, EntityHitboxManager.HitboxData hitboxData);
    }
}
