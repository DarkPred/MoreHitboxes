package com.github.darkpred.multipartsupport;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;

@ApiStatus.Internal
public interface MultiPartServerLevel {
    Collection<Entity> multiPartSupport$getMultiParts();

    void multiPartSupport$addMultiPart(Entity part);

    void multiPartSupport$removeMultiPart(Entity part);
}
