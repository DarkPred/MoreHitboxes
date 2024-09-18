package com.github.darkpred.multipartsupport.mixin;

import com.github.darkpred.multipartsupport.MultiPartEntityHitResult;
import com.github.darkpred.multipartsupport.entity.MultiPart;
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
    private MultiPart<?> multiPartSupport$part;

    @Override
    public void multiPartSupport$setMultiPart(MultiPart<?> part) {
        this.multiPartSupport$part = part;
    }

    @Override
    public MultiPart<?> multiPartSupport$getMultiPart() {
        return multiPartSupport$part;
    }
}
