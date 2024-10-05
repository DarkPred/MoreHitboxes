package com.github.darkpred.morehitboxes.api;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

/**
 * This interface is implemented by forge's {@code PartEntity} and a custom equivalent on fabric. Instances of this can
 * be gained by calling {@link EntityHitboxData#getCustomParts()} or {@link EntityHitboxData#getCustomPart(String)}}
 *
 * @param <T> the type of the parent entity
 */
public interface MultiPart<T extends Mob & MultiPartEntity<T>> {

    /**
     * @return the name of the hitbox cube
     */
    String getPartName();

    /**
     * @return the entity this part belongs to
     */
    T getParent();

    /**
     * @return {@code this} but cast as an Entity
     */
    Entity getEntity();

    /**
     * Used only if no GeckoLib bone has been set
     *
     * @return the initial local position defined in {@link HitboxData#pos()}
     */
    Vec3 getOffset();

    void setOverride(AnimationOverride animationOverride);

    AnimationOverride getOverride();

    @ApiStatus.Internal
    default void updatePosition() {
        Entity entity = getEntity();
        //entity.level.getProfiler().push("MultiPartUpdate");
        entity.xo = entity.getX();
        entity.yo = entity.getY();
        entity.zo = entity.getZ();
        entity.xOld = entity.xo;
        entity.yOld = entity.yo;
        entity.zOld = entity.zo;
        AnimationOverride animationOverride = getOverride();
        Vec3 newPos;
        if (animationOverride != null) {
            newPos = getParent().position().add(animationOverride.localPos());
            //Maybe unset animationOverride? I'm not sure if there is a scenario where the override is only set sometimes
        } else {
            Vec3 offset = getOffset();
            newPos = getParent().position().add(new Vec3(offset.x, offset.y, offset.z).yRot(-getParent().yBodyRot * Mth.DEG_TO_RAD).scale(getParent().getScale()));
        }
        entity.setPos(newPos);
        //entity.level.getProfiler().pop();
    }

    @ApiStatus.Internal
    interface Factory {
        <T extends Mob & MultiPartEntity<T>> MultiPart<T> create(T parent, HitboxData hitboxData);
    }
}
