package com.github.darkpred.multipartsupport.mixin;

import com.github.darkpred.multipartsupport.MultiPartEntityHitResult;
import com.github.darkpred.multipartsupport.entity.MultiPart;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Uses {@link MultiPartEntityHitResult} to add the correct part to the EntityHitResult
 */
@Mixin(ProjectileUtil.class)
public abstract class ProjectileUtilMixin {

    @Inject(method = "getEntityHitResult(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;D)Lnet/minecraft/world/phys/EntityHitResult;", at = @At("RETURN"), cancellable = true)
    private static void modifyPartEntity(CallbackInfoReturnable<EntityHitResult> cir) {
        if (cir.getReturnValue() == null) {
            return;
        }
        if (cir.getReturnValue().getEntity() instanceof MultiPart<?> part) {
            EntityHitResult hitResult = new EntityHitResult(part.getParent(), cir.getReturnValue().getLocation());
            ((MultiPartEntityHitResult)hitResult).multiPartSupport$setMultiPart(part);
            cir.setReturnValue(hitResult);
        }
    }
}
