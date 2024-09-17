package com.github.darkpred.multipartsupport;

import com.github.darkpred.multipartsupport.mixin.ServerLevelMixin;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Fabric has no PartEntity so we mixin our own
 *
 * @see ServerLevelMixin
 */
public interface MultiPartServerLevel {
    default Collection<Entity> fossilsArcheologyRevival$getMultiParts() {
        return new ArrayList<>();
    }

    default void fossilsArcheologyRevival$addMultiPart(Entity part) {
    }

    default void fossilsArcheologyRevival$removeMultiPart(Entity part) {

    }
}
