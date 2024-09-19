package com.github.darkpred.multipartsupport.entity;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

public interface GeckoLibMultiPartEntity<T extends Mob & MultiPartEntity<T>> extends MultiPartEntity<T> {

    void setAnchorPos(String boneName, Vec3 localPos);

    boolean canSetAnchorPos(String boneName);
}
