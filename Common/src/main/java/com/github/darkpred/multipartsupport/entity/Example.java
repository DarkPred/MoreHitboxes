package com.github.darkpred.multipartsupport.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class Example extends Mob implements MultiPartEntity<Example> {

    private final PlaceHolderName<Example> placeHolderName = PlaceHolderName.create(this);

    protected Example(EntityType<? extends Example> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public PlaceHolderName<Example> getPlaceHolderName() {
        return placeHolderName;
    }

    @Override
    public boolean partHurt(MultiPart<?> multiPart, @NotNull DamageSource source, float amount) {
        return hurt(source, amount);
    }
}
