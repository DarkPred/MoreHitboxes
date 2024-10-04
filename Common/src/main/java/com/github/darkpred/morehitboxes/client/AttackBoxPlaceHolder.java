package com.github.darkpred.multipartsupport.client;

import com.github.darkpred.multipartsupport.api.IAttackBoxPlaceHolder;
import com.github.darkpred.multipartsupport.entity.EntityHitboxManager;
import com.github.darkpred.multipartsupport.entity.MultiPartEntity;
import com.github.darkpred.multipartsupport.platform.Services;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

//TODO: Javadoc
public class AttackBoxPlaceHolder<T extends Mob & MultiPartEntity<T>> implements IAttackBoxPlaceHolder {
    private final Map<String, EntityHitboxManager.HitboxData> attackBoxes = new HashMap<>();
    private final Map<EntityHitboxManager.HitboxData, Vec3> activeAttackBoxes = new HashMap<>();
    private long attackBoxEndTime;
    private final T entity;

    public AttackBoxPlaceHolder(T entity) {
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
    public void moveActiveAttackBox(EntityHitboxManager.HitboxData attackBox, Vec3 worldPos) {
        activeAttackBoxes.put(attackBox, worldPos);
    }

    @Override
    public boolean isAttackBoxActive(EntityHitboxManager.HitboxData attackBox) {
        return activeAttackBoxes.containsKey(attackBox);
    }

    @Override
    public void activateAttackBoxes(Level level, double attackDuration) {
        attackBoxes.values().forEach(hitbox -> activeAttackBoxes.put(hitbox, Vec3.ZERO));
        attackBoxEndTime = (long) (level.getGameTime() + attackDuration);
    }

    @Override
    public void clientTick(Level level) {
        if (level.getGameTime() > attackBoxEndTime) {
            activeAttackBoxes.clear();
        }
        for (Map.Entry<EntityHitboxManager.HitboxData, Vec3> entry : activeAttackBoxes.entrySet()) {
            EntityHitboxManager.HitboxData hitbox = entry.getKey();
            EntityDimensions size = EntityDimensions.scalable(hitbox.width(), hitbox.height()).scale(entity.getScale());
            AABB aabb = size.makeBoundingBox(entry.getValue());
            Player player = DistUtilFactory.DIST_UTIL.handleIntersect(aabb);
            if (player != null) {
                if (entity.attackBoxHit(player)) {
                    activeAttackBoxes.clear();
                }
                break;
            }
        }
    }

    @Override
    public Map<EntityHitboxManager.HitboxData, Vec3> getActiveBoxes() {
        return activeAttackBoxes;
    }

    @Override
    public long attackBoxEndTime() {
        return attackBoxEndTime;
    }

    @ApiStatus.Internal
    public interface DistUtilFactory {
        DistUtilFactory DIST_UTIL = Services.load(DistUtilFactory.class);

        Player handleIntersect(AABB aabb);
    }
}
