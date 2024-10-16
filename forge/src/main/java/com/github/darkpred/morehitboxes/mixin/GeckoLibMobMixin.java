package com.github.darkpred.morehitboxes.mixin;

import com.github.darkpred.morehitboxes.internal.GeckoLibMultiPartMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Mob.class)
public abstract class GeckoLibMobMixin extends LivingEntity implements GeckoLibMultiPartMob {
    @Unique
    private int moreHitboxes$renderTick;

    protected GeckoLibMobMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public boolean moreHitboxes$isNewRenderTick() {
        return moreHitboxes$renderTick < tickCount;
    }

    @Override
    public void moreHitboxes$updateRenderTick() {
        moreHitboxes$renderTick = tickCount;
    }
}
