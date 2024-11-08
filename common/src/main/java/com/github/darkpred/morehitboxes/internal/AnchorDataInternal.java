package com.github.darkpred.morehitboxes.internal;

import com.github.darkpred.morehitboxes.api.AnchorData;
import com.github.darkpred.morehitboxes.api.HitboxData;
import com.github.darkpred.morehitboxes.api.MultiPartEntity;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@ApiStatus.Internal
public class AnchorDataInternal<T extends Mob & MultiPartEntity<T>> implements AnchorData {
    private final Map<String, HitboxData> anchors = new Object2ObjectOpenHashMap<>();
    private final Set<String> anchorOverride = new ObjectArraySet<>();
    private final Map<HitboxData, Vec3> anchorPositions = new Object2ObjectOpenHashMap<>();
    private final T entity;

    public AnchorDataInternal(T entity) {
        this.entity = entity;
    }

    @Override
    public Optional<Vec3> getAnchorPos(String ref) {
        return Optional.ofNullable(anchorPositions.get(anchors.get(ref)));
    }

    @Override
    public boolean isAnchor(String ref) {
        return anchors.containsKey(ref);
    }

    @Override
    public void addAnchor(String ref, HitboxData hitboxData) {
        anchors.put(ref, hitboxData);
    }

    @Override
    public void updatePositions() {
        for (Map.Entry<String, HitboxData> entry : anchors.entrySet()) {
            if (anchorOverride.contains(entry.getKey())) {
                anchorOverride.remove(entry.getKey());
            } else {
                Vec3 offset = entry.getValue().pos();
                Vec3 newPos;
                newPos = entity.position().add(new Vec3(offset.x, offset.y, offset.z).yRot(-entity.yBodyRot * Mth.DEG_TO_RAD).scale(entity.getScale()));
                anchorPositions.put(entry.getValue(), newPos);
            }
        }
    }

    @Override
    public void updatePosition(String ref, Vec3 localPos) {
        anchorPositions.put(anchors.get(ref), entity.position().add(localPos));
        //Need to override because GeckoLib mixin calls this method after entity tick but before updatePositions
        anchorOverride.add(ref);
    }
}
