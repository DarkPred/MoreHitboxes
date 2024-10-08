package com.github.darkpred.morehitboxes.internal;

import com.google.auto.service.AutoService;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@AutoService(AttackBoxDataInternal.DistUtilFactory.class)
public class ForgeDistUtil implements AttackBoxDataInternal.DistUtilFactory {

    @Override
    public Player handleIntersect(AABB aabb) {
        Player player = Minecraft.getInstance().player;
        if (player != null && player.getBoundingBox().intersects(aabb)) {
            return player;
        }
        return null;
    }
}
