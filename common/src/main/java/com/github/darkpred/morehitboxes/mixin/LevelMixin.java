package com.github.darkpred.morehitboxes.mixin;

import com.github.darkpred.morehitboxes.api.MultiPart;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * MultiParts can only be added if the test is also true for the parent.
 */
@Mixin(Level.class)
public class LevelMixin {

    @Inject(method = "getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;",
            at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/world/level/entity/LevelEntityGetter;get(Lnet/minecraft/world/phys/AABB;Ljava/util/function/Consumer;)V"))
    public void limitMultiPartInEntityQuery(Entity pEntity, AABB pBoundingBox, Predicate<Entity> predicate, CallbackInfoReturnable<List<Entity>> cir, @Local List<Entity> list) {
        Set<Entity> set = new HashSet<>();
        for (Entity entity : list) {
            if (entity instanceof MultiPart<?> part && (part.getParent() == pEntity || (!predicate.test(part.getParent()) || !predicate.test(entity)))) {
                set.add(entity);
            }
        }
        list.removeIf(set::contains);
    }
}
