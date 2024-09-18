package com.github.darkpred.multipartsupport.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.NotNull;

public interface MultiPartEntity<T extends Mob & MultiPartEntity<T>> {
    PlaceHolderName<T> getPlaceHolderName();

    boolean partHurt(MultiPart<?> multiPart, @NotNull DamageSource source, float amount);
}
