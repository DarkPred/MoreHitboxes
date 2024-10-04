package com.github.darkpred.multipartsupport.entity;

import com.github.darkpred.multipartsupport.api.IAttackBoxPlaceHolder;
import com.github.darkpred.multipartsupport.api.IPlaceHolderName;
import com.github.darkpred.multipartsupport.client.AttackBoxPlaceHolder;
import com.github.darkpred.multipartsupport.platform.Services;
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
public class PlaceHolderName<T extends Mob & com.github.darkpred.multipartsupport.entity.MultiPartEntity<T>> implements IPlaceHolderName<T> {
    private final List<MultiPart<T>> parts = new ArrayList<>();
    private final Map<String, MultiPart<T>> partsByRef = new HashMap<>();
    private final T entity;
    private final IAttackBoxPlaceHolder attackBoxPlaceHolder;
    private final boolean fixPosOnRefresh;
    private final boolean usesAttackBounds;
    private AABB attackBounds = new AABB(0, 0, 0, 0, 0, 0);
    private AABB cullingBounds = new AABB(0, 0, 0, 0, 0, 0);
    private float headRadius;
    private float frustumWidthRadius;
    private float frustumHeight;

    public PlaceHolderName(T entity, boolean fixPosOnRefresh, boolean usesAttackBounds) {
        this.entity = entity;
        this.attackBoxPlaceHolder = new AttackBoxPlaceHolder<>(entity);
        this.fixPosOnRefresh = fixPosOnRefresh;
        this.usesAttackBounds = usesAttackBounds;
        List<com.github.darkpred.multipartsupport.entity.EntityHitboxManager.HitboxData> hitboxData = com.github.darkpred.multipartsupport.entity.EntityHitboxManager.HITBOX_DATA.getHitboxes(EntityType.getKey(entity.getType()));
        if (hitboxData != null && !hitboxData.isEmpty()) {
            spawnHitBoxes(hitboxData);
        }
        makeAttackBounds();
        makeBoundingBoxForCulling();
    }

    private void spawnHitBoxes(List<com.github.darkpred.multipartsupport.entity.EntityHitboxManager.HitboxData> hitboxesData) {
        float maxFrustumWidthRadius = 0;
        float maxFrustumHeight = 0;
        //TODO: Clean up
        for (com.github.darkpred.multipartsupport.entity.EntityHitboxManager.HitboxData hitboxData : hitboxesData) {
            if (hitboxData.isAttackBox()) {
                attackBoxPlaceHolder.addAttackBox(hitboxData.ref(), hitboxData);
            } else {
                com.github.darkpred.multipartsupport.entity.MultiPart<T> part = Services.MULTI_PART.create(entity, hitboxData);
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
    public IAttackBoxPlaceHolder getAttackBoxPlaceHolder() {
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
