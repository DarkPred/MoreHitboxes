package com.github.darkpred.multipartsupport.entity;

import com.github.darkpred.multipartsupport.registration.RegistrationProvider;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Store {
    private final List<MultiPart> parts = new ArrayList<>();
    private final Map<String, MultiPart> partsByRef = new HashMap<>();

    private float headRadius;
    private float frustumWidthRadius;
    private float frustumHeightRadius;

    public Store(ResourceLocation path) {
        List<EntityHitboxManager.Hitbox> hitboxes = EntityHitboxManager.HITBOX_DATA.getHitboxes(path);
        if (hitboxes!= null && !hitboxes.isEmpty()) {
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
                MultiPart part = MultiPart.get(this, hitbox);
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
    public static Store get(ResourceLocation path) {
        return new Store(path);
    }
}
