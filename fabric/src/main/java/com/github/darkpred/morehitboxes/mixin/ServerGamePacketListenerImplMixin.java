package com.github.darkpred.morehitboxes.mixin;

import com.github.darkpred.morehitboxes.api.MultiPart;
import com.github.darkpred.morehitboxes.api.MultiPartEntity;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {

    @Shadow
    public ServerPlayer player;

    @ModifyExpressionValue(method = "handleInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/AABB;distanceToSqr(Lnet/minecraft/world/phys/Vec3;)D"))
    private double modifyForMultiPartEntities(double original, @Local Entity target) {
        if (target instanceof MultiPartEntity || target instanceof MultiPart<?>) {
            return isCloseEnough(target, player.isCreative() ? 9 : 6) ? 0 : Double.MAX_VALUE;
        }
        return original;
    }

    @Unique
    private boolean isCloseEnough(Entity entity, double dist) {
        //Copy forges extended attack and interact range for larger entities
        Vec3 eye = player.getEyePosition();
        AABB aabb = entity.getBoundingBox().inflate(entity.getPickRadius());
        return aabb.distanceToSqr(eye) < dist * dist;
    }
}
