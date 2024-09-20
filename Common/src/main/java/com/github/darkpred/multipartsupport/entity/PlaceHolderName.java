package com.github.darkpred.multipartsupport.entity;

import com.github.darkpred.multipartsupport.api.IAttackBoxPlaceHolder;
import com.github.darkpred.multipartsupport.client.AttackBoxPlaceholder;
import com.github.darkpred.multipartsupport.platform.Services;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//Not API
public class PlaceHolderName<T extends Mob & MultiPartEntity<T>> {
    private final List<MultiPart<T>> parts = new ArrayList<>();
    private final Map<String, MultiPart<T>> partsByRef = new HashMap<>();
    private final T entity;
    public final IAttackBoxPlaceHolder attackBoxPlaceholder;
    public final boolean fixPosOnRefresh;
    public final boolean usesAttackBounds;
    private AABB attackBounds = new AABB(0, 0, 0, 0, 0, 0);
    private AABB cullingBounds = new AABB(0, 0, 0, 0, 0, 0);
    private float headRadius;
    private float frustumWidthRadius;
    private float frustumHeightRadius;

    public PlaceHolderName(T entity, boolean fixPosOnRefresh, boolean usesAttackBounds) {
        this.entity = entity;
        this.attackBoxPlaceholder = new AttackBoxPlaceholder<>(entity);
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
                attackBoxPlaceholder.addAttackBox(hitboxData.ref(), hitboxData);
            } else {
                MultiPart<T> part = Services.MULTI_PART.create(entity, hitboxData);
                parts.add(part);
                if (!hitboxData.ref().isBlank()) {
                    partsByRef.put(hitboxData.ref(), part);
                }
                float w = hitboxData.getFrustumWidthRadius();
                if (hitboxData.name().contains("head") && (headRadius == 0 || w > maxFrustumWidthRadius)) {
                    headRadius = w;
                }
                if (w > maxFrustumWidthRadius) {
                    maxFrustumWidthRadius = w;
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
            cullingBounds = entity.makeBoundingBoxForCulling(frustumWidthRadius, frustumHeightRadius);
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
        attackBounds = entity.makeAttackBox(getHeadRadius());
    }

    public AABB getAttackBounds() {
        return attackBounds;
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
