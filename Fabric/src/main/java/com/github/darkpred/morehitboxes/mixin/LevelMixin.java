package com.github.darkpred.multipartsupport.mixin;

import com.github.darkpred.multipartsupport.entity.MultiPart;
import com.github.darkpred.multipartsupport.entity.MultiPartEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Predicate;

/**
 * Equivalent to what forge does with PartEntity
 */
@Mixin(Level.class)
public abstract class LevelMixin {

    @Shadow
    protected abstract LevelEntityGetter<Entity> getEntities();

    @Inject(method = "getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;", at = @At(value = "RETURN"))
    private void addMultiPartsToEntityQuery(Entity entity, AABB area, Predicate<? super Entity> predicate, CallbackInfoReturnable<List<Entity>> cir) {
        getEntities().get(area, entity2 -> {
            if (entity2 instanceof MultiPartEntity<?> multiPartEntity) {
                for (MultiPart<?> part : multiPartEntity.getPlaceHolderName().getCustomParts()) {
                    Entity partEntity = part.getEntity();
                    if (partEntity == entity || !partEntity.getBoundingBox().intersects(area) || !predicate.test(partEntity)) continue;
                    cir.getReturnValue().add(partEntity);
                }
            }
        });
    }

    @Inject(method = "getEntities(Lnet/minecraft/world/level/entity/EntityTypeTest;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;", at = @At(value = "RETURN"))
    private <T extends Entity> void addMultiPartsToEntityQuery(EntityTypeTest<Entity, T> entityTypeTest, AABB area, Predicate<? super T> predicate,
                                                               CallbackInfoReturnable<List<T>> cir) {
        getEntities().get(entityTypeTest, area, entity -> {
            if (entity instanceof MultiPartEntity<?> multiPartEntity) {
                for (MultiPart<?> part : multiPartEntity.getPlaceHolderName().getCustomParts()) {
                    Entity partEntity = part.getEntity();
                    T entity2 = entityTypeTest.tryCast(partEntity);
                    if (entity2 == null || !predicate.test(entity2)) continue;
                    cir.getReturnValue().add(entity2);
                }
            }
        });
    }
}
