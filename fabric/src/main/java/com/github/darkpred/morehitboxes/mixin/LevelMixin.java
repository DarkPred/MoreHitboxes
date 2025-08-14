package com.github.darkpred.morehitboxes.mixin;

import com.github.darkpred.morehitboxes.MultiPartLevel;
import com.github.darkpred.morehitboxes.api.MultiPart;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
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
 * Equivalent to what forge does with PartEntity
 */
@Mixin(Level.class)
public abstract class LevelMixin implements MultiPartLevel {

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

    @Inject(method = "getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;",
            at = @At(value = "RETURN"))
    private void addMultiPartsToEntityQuery(Entity entity, AABB area, Predicate<? super Entity> predicate, CallbackInfoReturnable<List<Entity>> cir, @Local List<Entity> list) {
        for (MultiPart<?> part : moreHitboxes$getMultiParts()) {
            Entity partEntity = part.getEntity();
            Entity parent = part.getParent();
            if (parent != entity && partEntity.getBoundingBox().intersects(area) && predicate.test(partEntity) && predicate.test(parent)) {
                list.add(partEntity);
            }
        }
    }

    @Inject(method = "getEntities(Lnet/minecraft/world/level/entity/EntityTypeTest;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;", at = @At(value = "RETURN"))
    private <T extends Entity> void addMultiPartsToEntityQuery(EntityTypeTest<Entity, T> entityTypeTest, AABB area, Predicate<? super T> predicate, CallbackInfoReturnable<List<T>> cir, @Local List<Entity> list) {
        for (MultiPart<?> part : moreHitboxes$getMultiParts()) {
            T parent = entityTypeTest.tryCast(part.getParent());
            //No check for the MultiPart entity itself
            if (parent != null && !list.contains(parent) && part.getEntity().getBoundingBox().intersects(area) && predicate.test(parent)) {
                list.add(parent);
            }
        }
    }
}
