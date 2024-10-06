package com.github.darkpred.morehitboxes.internal;

import com.github.darkpred.morehitboxes.api.*;
import com.github.darkpred.morehitboxes.platform.Services;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApiStatus.Internal
public class EntityHitboxDataInternal<T extends Mob & MultiPartEntity<T>> implements EntityHitboxData<T> {
    private final List<MultiPart<T>> parts = new ArrayList<>();
    private final Map<String, MultiPart<T>> partsByRef = new HashMap<>();
    private final T entity;
    private final AttackBoxData attackBoxPlaceHolder;
    private final boolean fixPosOnRefresh;
    private final boolean usesAttackBounds;
    private AABB attackBounds = new AABB(0, 0, 0, 0, 0, 0);
    private AABB cullingBounds = new AABB(0, 0, 0, 0, 0, 0);
    private float headRadius;
    private float frustumWidthRadius;
    private float frustumHeight;

    public EntityHitboxDataInternal(T entity, boolean fixPosOnRefresh, boolean usesAttackBounds) {
        this.entity = entity;
        this.attackBoxPlaceHolder = new AttackBoxDataInternal<>(entity);
        this.fixPosOnRefresh = fixPosOnRefresh;
        this.usesAttackBounds = usesAttackBounds;
        List<HitboxData> hitboxData = HitboxDataLoader.HITBOX_DATA.getHitboxes(EntityType.getKey(entity.getType()));
        if (hitboxData != null && !hitboxData.isEmpty()) {
            spawnHitBoxes(hitboxData);
        }
        makeAttackBounds();
        makeBoundingBoxForCulling();
    }

    private void spawnHitBoxes(List<HitboxData> hitboxesData) {
        float maxFrustumWidthRadius = 0;
        float maxFrustumHeight = 0;
        for (HitboxData hitboxData : hitboxesData) {
            if (hitboxData.isAttackBox()) {
                attackBoxPlaceHolder.addAttackBox(hitboxData.ref(), hitboxData);
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
                float h = hitboxData.getFrustumHeight();
                if (h > maxFrustumHeight) {
                    maxFrustumHeight = h;
                }
            }
        }
        frustumWidthRadius = maxFrustumWidthRadius;
        frustumHeight = maxFrustumHeight;
    }

    @Override
    public AttackBoxData getAttackBoxPlaceHolder() {
        return attackBoxPlaceHolder;
    }

    @Override
    public void makeBoundingBoxForCulling() {
        if (hasCustomParts()) {
            cullingBounds = entity.makeBoundingBoxForCulling(frustumWidthRadius, frustumHeight);
        }
    }

    @Override
    public AABB getCullingBounds() {
        return cullingBounds;
    }

    @Override
    public void makeAttackBounds() {
        if (!usesAttackBounds) {
            return;
        }
        attackBounds = entity.makeAttackBoundingBox(getHeadRadius() * entity.getScale());
    }

    @Override
    public AABB getAttackBounds() {
        return attackBounds;
    }

    @Override
    public float getHeadRadius() {
        return headRadius;
    }

    @Override
    public boolean hasCustomParts() {
        return !parts.isEmpty();
    }

    @Override
    public List<MultiPart<T>> getCustomParts() {
        return parts;
    }

    @Override
    public @Nullable MultiPart<T> getCustomPart(String ref) {
        return partsByRef.get(ref);
    }

    @Override
    public boolean fixPosOnRefresh() {
        return fixPosOnRefresh;
    }
}
