package com.github.darkpred.multipartsupport.entity;

import net.minecraft.world.entity.Entity;
//API
public interface MultiPartGeoEntityRenderer {

    void removeTickForEntity(Entity entity);

    void updateTickForEntity(Entity entity);
}
