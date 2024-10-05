package com.github.darkpred.morehitboxes.mixin;

import com.github.darkpred.morehitboxes.MoreHitboxesMod;
import com.github.darkpred.morehitboxes.api.MultiPart;
import com.github.darkpred.morehitboxes.api.MultiPartEntity;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.*;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(TransientEntitySectionManager.class)
public abstract class TransientEntitySectionManagerMixin<T extends EntityAccess> {
    @Shadow
    @Final
    EntitySectionStorage<T> sectionStorage;

    /**
     * Vanilla hit results stop working if the bounding box of an entity is in an empty EntitySection.
     * There are multiple ways to get around this and in this case the implementation manually adds/removes the part
     * entities to/from their correct EntitySections.
     *
     * @see EntitySection
     * @see EntitySectionStorage#getEntities(AABB, Consumer)
     */
    @Inject(method = "addEntity", at = @At("HEAD"))
    private void addPartEntitiesToSections(T entity, CallbackInfo ci) {
        if (entity instanceof MultiPartEntity<?> multiPartEntity && multiPartEntity.getPlaceHolderName().hasCustomParts()) {
            for (MultiPart<?> part : multiPartEntity.getPlaceHolderName().getCustomParts()) {
                T partEntity = (T) part.getEntity();
                long l = SectionPos.asLong(partEntity.blockPosition());
                EntitySection<T> section = this.sectionStorage.getOrCreateSection(l);
                section.add(partEntity);
                //The anonymous class that the mixin creates for the callback will try to access sectionStorage with incorrect mappings
                final EntitySectionStorage<T> unmappedSectionStorage = sectionStorage;
                partEntity.setLevelCallback(new EntityInLevelCallback() {
                    private long currentSectionKey = l;
                    private EntitySection<T> currentSection = section;

                    @Override
                    public void onMove() {
                        long newSectionKey = SectionPos.asLong(partEntity.blockPosition());
                        if (newSectionKey != currentSectionKey) {
                            if (!currentSection.remove(partEntity)) {
                                MoreHitboxesMod.LOGGER.warn("MultiPart {} wasn't found in section {} (moving to {})", partEntity, SectionPos.of(currentSectionKey), newSectionKey);
                            }
                            removeSectionIfEmpty(currentSectionKey, currentSection);
                            EntitySection<T> newSection = unmappedSectionStorage.getOrCreateSection(newSectionKey);
                            newSection.add(partEntity);
                            currentSection = newSection;
                            currentSectionKey = newSectionKey;
                        }
                    }

                    @Override
                    public void onRemove(Entity.RemovalReason reason) {
                        if (!currentSection.remove(partEntity)) {
                            MoreHitboxesMod.LOGGER.warn("MultiPart {} wasn't found in section {} (destroying due to {})", partEntity, SectionPos.of(currentSectionKey), reason);
                        }
                        partEntity.setLevelCallback(NULL);
                        removeSectionIfEmpty(currentSectionKey, currentSection);
                    }
                });
            }
        }
    }

    @Shadow
    abstract void removeSectionIfEmpty(long section, EntitySection<T> entitySection);
}
