package com.github.darkpred.multipartsupport.mixin;

import com.github.darkpred.multipartsupport.entity.MultiPartEntityHitResult;
import com.github.darkpred.multipartsupport.entity.MultiPart;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Uses {@link MultiPartEntityHitResult} to add the correct part to the EntityHitResult
 */
@Mixin(ProjectileUtil.class)
public abstract class ProjectileUtilMixin {

    @ModifyReturnValue(method = "getEntityHitResult(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;D)Lnet/minecraft/world/phys/EntityHitResult;", at = @At("RETURN"))
    private static EntityHitResult modifyPartEntity(EntityHitResult original) {
        if (original == null) {
            return null;
        }
        if (original.getEntity() instanceof MultiPart<?> part) {
            EntityHitResult hitResult = new EntityHitResult(part.getParent(), original.getLocation());
            ((MultiPartEntityHitResult) hitResult).multiPartSupport$setMultiPart(part);
            return hitResult;
        }
        return original;
    }
}
