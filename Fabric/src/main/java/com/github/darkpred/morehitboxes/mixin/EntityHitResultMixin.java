package com.github.darkpred.morehitboxes.mixin;

import com.github.darkpred.morehitboxes.entity.MultiPart;
import com.github.darkpred.morehitboxes.entity.MultiPartEntityHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * Adds a {@link MultiPart} field that is used to determine the exact part hit during an attack
 */
@Mixin(EntityHitResult.class)
public abstract class EntityHitResultMixin implements MultiPartEntityHitResult {
    @Unique
    @Nullable
    private MultiPart<?> moreHitboxes$part;

    @Override
    public void moreHitboxes$setMultiPart(MultiPart<?> part) {
        this.moreHitboxes$part = part;
    }

    @Override
    public @Nullable MultiPart<?> moreHitboxes$getMultiPart() {
        return moreHitboxes$part;
    }
}
