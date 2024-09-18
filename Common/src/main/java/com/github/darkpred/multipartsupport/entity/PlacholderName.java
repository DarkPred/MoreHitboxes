package com.github.darkpred.multipartsupport.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlacholderName<T extends Mob & MultiPartEntity> {
    private final List<MultiPart<T>> parts = new ArrayList<>();
    private final Map<String, MultiPart<T>> partsByRef = new HashMap<>();
    //TODO: I think this would go into an api package
    private float headRadius;
    private float frustumWidthRadius;
    private float frustumHeightRadius;
    private final T entity;

    public PlacholderName(T entity, ResourceLocation path) {
        this.entity = entity;
        List<EntityHitboxManager.HitboxData> hitboxData = EntityHitboxManager.HITBOX_DATA.getHitboxes(path);
        if (hitboxData != null && !hitboxData.isEmpty()) {
            spawnHitBoxes(hitboxData);
        }
    }

    private void spawnHitBoxes(List<EntityHitboxManager.HitboxData> hitboxData) {
        float maxFrustumWidthRadius = 0;
        float maxFrustumHeightRadius = 0;
        for (EntityHitboxManager.HitboxData hitboxData : hitboxData) {
            if (hitboxData.isAttackBox()) {
                attackBoxes.put(hitboxData.ref(), hitboxData);
            } else {
                MultiPart<T> part = MultiPart.Factory.INSTANCE.create(entity, hitboxData);
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
     * @implSpec On the forge classpath this implementation should return objects that inherit from PartEntity instead of Entity.
     */
    public List<MultiPart<T>> getCustomParts() {
        return parts;
    }

    /**
     * @param ref the name of the bone the hitbox part is attached to
     * @return the hitbox part attached to the given bone
     */
    @Nullable
    public MultiPart<?> getCustomPart(String ref) {
        return partsByRef.get(ref);
    }

    public static <T extends Mob & MultiPartEntity> PlacholderName<T> get(T entity, ResourceLocation path) {
        return new PlacholderName<>(entity, path);
    }
}
