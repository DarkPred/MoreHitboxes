package com.github.darkpred.morehitboxes;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;

@ApiStatus.Internal
public interface MultiPartServerLevel {
    Collection<Entity> morehitboxes$getMultiParts();

    void morehitboxes$addMultiPart(Entity part);

    void morehitboxes$removeMultiPart(Entity part);
}
