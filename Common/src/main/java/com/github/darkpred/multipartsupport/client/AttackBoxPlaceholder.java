package com.github.darkpred.multipartsupport.client;

import com.github.darkpred.multipartsupport.api.IAttackBoxPlaceHolder;
import com.github.darkpred.multipartsupport.entity.EntityHitboxManager;
import com.github.darkpred.multipartsupport.entity.MultiPartEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public class AttackBoxPlaceholder<T extends Mob & MultiPartEntity<T>> implements IAttackBoxPlaceHolder {
    private final Map<String, EntityHitboxManager.HitboxData> attackBoxes = new HashMap<>();
    private final Map<EntityHitboxManager.HitboxData, Vec3> activeAttackBoxes = new HashMap<>();
    private long attackBoxEndTime;
    private final T entity;

    public AttackBoxPlaceholder(T entity) {
        this.entity = entity;
    }

    @Override
    public void addAttackBox(String ref, EntityHitboxManager.HitboxData hitboxData) {
        attackBoxes.put(ref, hitboxData);
    }

    @Override
    public EntityHitboxManager.HitboxData getAttackBox(String ref) {
        return attackBoxes.get(ref);
    }

    @Override
    public void moveAttackBox(EntityHitboxManager.HitboxData attackBox, Vec3 worldPos) {
        activeAttackBoxes.put(attackBox, worldPos);
    }

    @Override
    public boolean isAttackBoxActive(EntityHitboxManager.HitboxData attackBox) {
        return activeAttackBoxes.containsKey(attackBox);
    }

    @Override
    public void activateAttackBoxes(ClientLevel level, double attackDuration) {
        attackBoxes.values().forEach(hitbox -> activeAttackBoxes.put(hitbox, Vec3.ZERO));
        attackBoxEndTime = (long) (level.getGameTime() + attackDuration);
    }

    @Override
    public void clientTick(ClientLevel level) {
        if (level.getGameTime() > attackBoxEndTime) {
            activeAttackBoxes.clear();
        }
        for (Map.Entry<EntityHitboxManager.HitboxData, Vec3> entry : activeAttackBoxes.entrySet()) {
            EntityHitboxManager.HitboxData hitbox = entry.getKey();
            EntityDimensions size = EntityDimensions.scalable(hitbox.width(), hitbox.height()).scale(entity.getScale());
            AABB aabb = size.makeBoundingBox(entry.getValue());
            if (Minecraft.getInstance().player.getBoundingBox().intersects(aabb)) {
                if (entity.attackBoxHit(Minecraft.getInstance().player)) {
                    activeAttackBoxes.clear();
                }
                break;
            }
        }
    }

    @Override
    public long attackBoxEndTime() {
        return attackBoxEndTime;
    }
}
