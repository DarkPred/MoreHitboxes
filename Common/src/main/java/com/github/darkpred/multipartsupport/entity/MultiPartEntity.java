package com.github.darkpred.multipartsupport.entity;

import net.minecraft.world.damagesource.DamageSource;
import org.jetbrains.annotations.NotNull;

public interface MultiPartEntity {
    PlacholderName<?> getPlaceHolderName();

    boolean partHurt(MultiPart<?> multiPart, @NotNull DamageSource source, float amount);
}
