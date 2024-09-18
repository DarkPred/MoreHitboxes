package com.github.darkpred.multipartsupport;

import net.minecraft.world.entity.Entity;

import java.util.Collection;

public interface MultiPartServerLevel {
    Collection<Entity> multiPartSupport$getMultiParts();

    void multiPartSupport$addMultiPart(Entity part);

    void multiPartSupport$removeMultiPart(Entity part);
}
