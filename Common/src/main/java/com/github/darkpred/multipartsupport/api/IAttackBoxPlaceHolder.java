package com.github.darkpred.multipartsupport.api;

import com.github.darkpred.multipartsupport.entity.EntityHitboxManager;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.Vec3;

public interface IAttackBoxPlaceHolder {

    void addAttackBox(String ref, EntityHitboxManager.HitboxData hitboxData);

    EntityHitboxManager.HitboxData getAttackBox(String ref);

    void moveAttackBox(EntityHitboxManager.HitboxData attackBox, Vec3 worldPos);

    boolean isAttackBoxActive(EntityHitboxManager.HitboxData attackBox);

    void activateAttackBoxes(ClientLevel level, double attackDuration);

    void clientTick(ClientLevel level);

    long attackBoxEndTime();
}
