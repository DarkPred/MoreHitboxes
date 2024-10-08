package com.github.darkpred.morehitboxes.mixin;

import com.github.darkpred.morehitboxes.api.MultiPart;
import com.github.darkpred.morehitboxes.internal.MultiPartEntityHitResult;
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

    @ModifyReturnValue(method = "getEntityHitResult*", at = @At("RETURN"))
    private static EntityHitResult modifyPartEntity(EntityHitResult original) {
        if (original == null) {
            return null;
        }
        if (original.getEntity() instanceof MultiPart<?> part) {
            EntityHitResult hitResult = new EntityHitResult(part.getParent(), original.getLocation());
            ((MultiPartEntityHitResult) hitResult).moreHitboxes$setMultiPart(part);
            return hitResult;
        }
        return original;
    }
}
