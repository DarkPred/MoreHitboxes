package com.github.darkpred.multipartsupport.entity;

import com.github.darkpred.multipartsupport.platform.Services;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaceHolderName<T extends Mob & MultiPartEntity<T>> {
    private final List<MultiPart<T>> parts = new ArrayList<>();
    private final Map<String, MultiPart<T>> partsByRef = new HashMap<>();
    //TODO: I think this would go into an api package
    private final T entity;
    public final boolean fixPosOnRefresh;
    public final boolean usesAttackBounds;
    private AABB attackBounds = new AABB(0, 0, 0, 0, 0, 0);
    private AABB cullingBounds = new AABB(0, 0, 0, 0, 0, 0);
    private float headRadius;
    private float frustumWidthRadius;
    private float frustumHeightRadius;

    public PlaceHolderName(T entity, boolean fixPosOnRefresh, boolean usesAttackBounds) {
        this.entity = entity;
        this.fixPosOnRefresh = fixPosOnRefresh;
        this.usesAttackBounds = usesAttackBounds;
        List<EntityHitboxManager.HitboxData> hitboxData = EntityHitboxManager.HITBOX_DATA.getHitboxes(EntityType.getKey(entity.getType()));
        if (hitboxData != null && !hitboxData.isEmpty()) {
            spawnHitBoxes(hitboxData);
        }
        makeAttackBounds();
        makeBoundingBoxForCulling();
    }

    private void spawnHitBoxes(List<EntityHitboxManager.HitboxData> hitboxesData) {
        float maxFrustumWidthRadius = 0;
        float maxFrustumHeightRadius = 0;
        for (EntityHitboxManager.HitboxData hitboxData : hitboxesData) {
            if (hitboxData.isAttackBox()) {
                //attackBoxes.put(hitboxData.ref(), hitboxData);
            } else {
                MultiPart<T> part = Services.MULTI_PART.create(entity, hitboxData);
                parts.add(part);
                if (hitboxData.ref() != null) {
                    partsByRef.put(hitboxData.ref(), part);
                }
                //Caching this value might be overkill but this ensures that the entity will be visible even if parts are outside its bounding box
                float j = hitboxData.getFrustumWidthRadius();
                if (hitboxData.name().contains("head") && (headRadius == 0 || j > maxFrustumWidthRadius)) {
                    headRadius = j;
                }
                if (j > maxFrustumWidthRadius) {
                    maxFrustumWidthRadius = j;
                }
                float h = hitboxData.getFrustumHeightRadius();
                if (h > maxFrustumHeightRadius) {
                    maxFrustumHeightRadius = h;
                }
            }
        }
        frustumWidthRadius = maxFrustumWidthRadius;
        frustumHeightRadius = maxFrustumHeightRadius;
    }

    public void makeBoundingBoxForCulling() {
        if (hasCustomParts()) {
            float x = frustumWidthRadius * entity.getScale();
            float y = frustumHeightRadius * entity.getScale();
            Vec3 pos = entity.position();
            cullingBounds = new AABB(pos.x - x, pos.y, pos.z - x, pos.x + x, pos.y + y, pos.z + x);
        } else {
            cullingBounds = entity.getBoundingBoxForCulling();
        }
    }

    public AABB getCullingBounds() {
        return cullingBounds;
    }

    public void makeAttackBounds() {
        if (!usesAttackBounds) {
            return;
        }
        if (headRadius == 0) {
            float increase = Math.min(entity.getBbWidth() / 2, 2.25f);
            attackBounds = inflateAABB(entity.getBoundingBox(), increase, increase, increase);
        } else {
            float radius = headRadius * entity.getScale() * 0.9f;
            attackBounds = inflateAABB(entity.getBoundingBox(), radius, radius * 0.55, radius);
        }
    }

    public AABB getAttackBounds() {
        return attackBounds;
    }

    private AABB inflateAABB(AABB base, double x, double y, double z) {
        return new AABB(base.minX - x, base.minY - Math.min(1, y), base.minZ - z, base.maxX + x, base.maxY + y, base.maxZ + z);
    }

    public float getHeadRadius() {
        return headRadius * entity.getScale();
    }

    //TODO: Javadoc

    /**
     * Returns {@code true} if
     *
     * @return {@code true} if
     */
    public boolean hasCustomParts() {
        return !parts.isEmpty();
    }

    /**
     * @return The hitbox parts of this entity.
     */
    public List<MultiPart<T>> getCustomParts() {
        return parts;
    }

    /**
     * @param ref the name of the bone the hitbox part is attached to
     * @return the hitbox part attached to the given bone
     */
    @Nullable
    public MultiPart<T> getCustomPart(String ref) {
        return partsByRef.get(ref);
    }

    public static <T extends Mob & MultiPartEntity<T>> PlaceHolderName<T> create(T entity, boolean fixPosOnRefresh, boolean usesAttackBounds) {
        return new PlaceHolderName<>(entity, fixPosOnRefresh, usesAttackBounds);
    }

    public static <T extends Mob & MultiPartEntity<T>> PlaceHolderName<T> create(T entity) {
        return create(entity, true, true);
    }
}
