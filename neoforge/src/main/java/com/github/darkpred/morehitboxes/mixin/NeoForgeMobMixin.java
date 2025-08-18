package com.github.darkpred.morehitboxes.mixin;

import com.github.darkpred.morehitboxes.api.MultiPart;
import com.github.darkpred.morehitboxes.api.MultiPartEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.entity.PartEntity;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(Mob.class)
public abstract class NeoForgeMobMixin extends LivingEntity {

    protected NeoForgeMobMixin(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public PartEntity<?>[] getParts() {
        if (this instanceof MultiPartEntity<?> multiPartEntity) {
            List<? extends MultiPart<?>> parts = multiPartEntity.getEntityHitboxData().getCustomParts();
            PartEntity<?>[] ret = new PartEntity[parts.size()];
            for (int i = 0; i < parts.size(); i++) {
                ret[i] = (PartEntity<?>) parts.get(i).getEntity();
            }
            return ret;
        }
        return super.getParts();
    }

    @Override
    public boolean isMultipartEntity() {
        if (this instanceof MultiPartEntity<?> multiPartEntity) {
            return multiPartEntity.getEntityHitboxData().hasCustomParts();
        }
        return super.isMultipartEntity();
    }
}
