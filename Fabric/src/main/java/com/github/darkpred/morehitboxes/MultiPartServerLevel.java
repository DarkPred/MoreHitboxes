package com.github.darkpred.morehitboxes;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;

@ApiStatus.Internal
public interface MultiPartServerLevel {
    Collection<Entity> moreHitboxes$getMultiParts();

    void moreHitboxes$addMultiPart(Entity part);

    void moreHitboxes$removeMultiPart(Entity part);
}
