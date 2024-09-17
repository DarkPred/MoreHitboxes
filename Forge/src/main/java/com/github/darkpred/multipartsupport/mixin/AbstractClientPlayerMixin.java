package com.github.darkpred.multipartsupport.mixin;

import com.github.darkpred.multipartsupport.entity.MultiPartEntity;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin extends Player {

    protected AbstractClientPlayerMixin(Level arg, BlockPos arg2, float f, GameProfile gameProfile) {
        super(arg, arg2, f, gameProfile);
    }

    @Override
    public boolean isCloseEnough(Entity entity, double dist) {
        if (super.isCloseEnough(entity, dist)) {
            return true;
        }
        if (entity instanceof MultiPartEntity prehistoric && prehistoric.isCustomMultiPart()) {
            for (MultiPart part : prehistoric.getCustomParts()) {
                if (isCloseEnough(part.getEntity(), dist)) {
                    return true;
                }
            }
        }
        return false;
    }
}
