package com.github.darkpred.multipartsupport.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

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
        List<EntityHitboxManager.Hitbox> hitboxes = EntityHitboxManager.HITBOX_DATA.getHitboxes(path);
        if (hitboxes != null && !hitboxes.isEmpty()) {
            spawnHitBoxes(hitboxes);
        }
    }

    private void spawnHitBoxes(List<EntityHitboxManager.Hitbox> hitboxes) {
        float maxFrustumWidthRadius = 0;
        float maxFrustumHeightRadius = 0;
        for (EntityHitboxManager.Hitbox hitbox : hitboxes) {
            if (hitbox.isAttackBox()) {
                attackBoxes.put(hitbox.ref(), hitbox);
            } else {
                MultiPart<T> part = MultiPart.Factory.INSTANCE.create(entity, hitbox);
                parts.add(part);
                if (hitbox.ref() != null) {
                    partsByRef.put(hitbox.ref(), part);
                }
                //Caching this value might be overkill but this ensures that the entity will be visible even if parts are outside its bounding box
                float j = hitbox.getFrustumWidthRadius();
                if (hitbox.name().contains("head") && (headRadius == 0 || j > maxFrustumWidthRadius)) {
                    headRadius = j;
                }
                if (j > maxFrustumWidthRadius) {
                    maxFrustumWidthRadius = j;
                }
                float h = hitbox.getFrustumHeightRadius();
                if (h > maxFrustumHeightRadius) {
                    maxFrustumHeightRadius = h;
                }
            }
        }
        frustumWidthRadius = maxFrustumWidthRadius;
        frustumHeightRadius = maxFrustumHeightRadius;
    }

    public boolean isCustomMultiPart() {
        return !parts.isEmpty();
    }

    /**
     * @return The child parts of this entity.
     * @implSpec On the forge classpath this implementation should return objects that inherit from PartEntity instead of Entity.
     */
    public List<MultiPart<T>> getCustomParts() {
        return parts;
    }

    public static <T extends Mob & MultiPartEntity> PlacholderName<T> get(T entity, ResourceLocation path) {
        return new PlacholderName<>(entity, path);
    }
}
