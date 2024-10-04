package com.github.darkpred.multipartsupport.entity;

import com.github.darkpred.multipartsupport.api.IPlaceHolderName;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/**
 * The base interface for mobs that want custom hitbox support. This interface exposes some methods that may be
 * overridden for more control
 * <p>
 * Required steps to use:
 * <ol>
 *     <li>Add one or more hitboxes in data/{modId}/hitboxes/{entityTypeKey}.json </li>
 *     <li>Implement this interface or a child interface </li>
 *     <li>Call and save {@link com.github.darkpred.multipartsupport.api.PlaceHolderNameFactory#create(Mob) PlaceHolderNameFactory#create(Mob)}</li>
 * </ol>
 *
 * @param <T> the type of the mob implementing this interface
 */
//TODO: Move to api
public interface MultiPartEntity<T extends Mob & MultiPartEntity<T>> {
    IPlaceHolderName<T> getPlaceHolderName();

    boolean partHurt(com.github.darkpred.multipartsupport.entity.MultiPart<T> multiPart, @NotNull DamageSource source, float amount);

    /**
     * The result of this method is not directly used by the library and can instead be accessed and used via {@link IPlaceHolderName#getAttackBounds()}
     *
     * @param scaledHeadRadius {@link IPlaceHolderName#getHeadRadius()} multiplied by {@link Mob#getScale()}
     * @apiNote the resulting box can be seen in a blue outline with the F3+B debug view
     */
    default AABB makeAttackBoundingBox(float scaledHeadRadius) {
        Mob mob = (Mob) this;
        if (scaledHeadRadius == 0) {
            float increase = Math.min(mob.getBbWidth() / 2, 2.25f);
            return inflateAABB(mob.getBoundingBox(), increase, increase, increase);
        } else {
            float radius = scaledHeadRadius * 0.9f;
            return inflateAABB(mob.getBoundingBox(), radius, radius * 0.55, radius);
        }
    }

    /**
     * It might make sense to override this method if the custom parts do not cover all of the mobs model
     *
     * @param frustumWidthRadius the horizontal distance from the farthest part to the center of the mob
     * @param frustumHeight      the vertical distance from the farthest part to the bottom of the mob
     * @apiNote the resulting box can be seen in a pink outline with the F3+B debug view
     * @implSpec the default implementation returns a bounding box extending to the farthest hitbox parts
     */
    default AABB makeBoundingBoxForCulling(float frustumWidthRadius, float frustumHeight) {
        Mob mob = (Mob) this;
        float x = frustumWidthRadius * mob.getScale();
        float y = frustumHeight * mob.getScale();
        Vec3 pos = mob.position();
        return new AABB(pos.x - x, pos.y, pos.z - x, pos.x + x, pos.y + y, pos.z + x);
    }

    private AABB inflateAABB(AABB base, double x, double y, double z) {
        return new AABB(base.minX - x, base.minY - Math.min(1, y), base.minZ - z, base.maxX + x, base.maxY + y, base.maxZ + z);
    }

    /**
     * Is called when the local player intersects with an active attack box. The code is purely clientside and there is
     * no coordination with the server
     * <p>
     * If {@code true} is returned the attack will be considered done and all active attack boxes cleared to prevent multi hits
     *
     * @param player the local player
     * @return {@code true} if all active attack boxes should be disabled
     * @implSpec the default implementation has no side effects and returns {@code true}
     */
    default boolean attackBoxHit(Player player) {
        return true;
    }
}
