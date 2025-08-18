package com.github.darkpred.morehitboxes.mixin;

import com.github.darkpred.morehitboxes.internal.MultiPartEntityHitResult;
import com.github.darkpred.morehitboxes.internal.ProjectileUtilOverride;
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
    private static EntityHitResult modifyLookEntity(EntityHitResult original) {
        return ProjectileUtilOverride.modifyPartEntity(original);
    }

    @ModifyReturnValue(method = "getEntityHitResult(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;F)Lnet/minecraft/world/phys/EntityHitResult;", at = @At("RETURN"))
    private static EntityHitResult modifyHitEntity(EntityHitResult original) {
        return ProjectileUtilOverride.modifyPartEntity(original);
    }
}
