package com.github.darkpred.multipartsupport.entity;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
//API
public interface MultiPartEntity<T extends Mob & MultiPartEntity<T>> {
    PlaceHolderName<T> getPlaceHolderName();

    boolean partHurt(MultiPart<T> multiPart, @NotNull DamageSource source, float amount);

    default AABB makeAttackBox(float scaledHeadRadius) {
        Mob mob = (Mob) this;
        if (scaledHeadRadius == 0) {
            float increase = Math.min(mob.getBbWidth() / 2, 2.25f);
            return inflateAABB(mob.getBoundingBox(), increase, increase, increase);
        } else {
            float radius = scaledHeadRadius * 0.9f;
            return inflateAABB(mob.getBoundingBox(), radius, radius * 0.55, radius);
        }
    }

    default AABB makeBoundingBoxForCulling(float frustumWidthRadius, float frustumHeightRadius) {
        Mob mob = (Mob) this;
        float x = frustumWidthRadius * mob.getScale();
        float y = frustumHeightRadius * mob.getScale();
        Vec3 pos = mob.position();
        return new AABB(pos.x - x, pos.y, pos.z - x, pos.x + x, pos.y + y, pos.z + x);
    }

    private AABB inflateAABB(AABB base, double x, double y, double z) {
        return new AABB(base.minX - x, base.minY - Math.min(1, y), base.minZ - z, base.maxX + x, base.maxY + y, base.maxZ + z);
    }

    default boolean attackBoxHit(LocalPlayer player) {
        return true;
    }
}
